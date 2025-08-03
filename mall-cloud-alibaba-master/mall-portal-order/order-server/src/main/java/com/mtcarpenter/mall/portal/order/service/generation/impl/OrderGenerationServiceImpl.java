package com.mtcarpenter.mall.portal.order.service.generation.impl;

import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.domain.SmsCouponHistoryDetail;
import com.mtcarpenter.mall.model.*;
import com.mtcarpenter.mall.portal.order.domain.ConfirmOrderResult;
import com.mtcarpenter.mall.portal.order.domain.OrderParam;
import com.mtcarpenter.mall.portal.order.service.cart.OmsCartItemService;
import com.mtcarpenter.mall.portal.order.service.cart.read.CartReadService;
import com.mtcarpenter.mall.portal.order.service.cart.write.CartWriteService;
import com.mtcarpenter.mall.portal.order.service.generation.OrderGenerationService;
import com.mtcarpenter.mall.security.service.RedisService;
import com.mtcarpenter.mall.common.exception.Asserts;
import com.mtcarpenter.provider.coupon.CouponProvider;
import com.mtcarpenter.provider.integration.IntegrationProvider;
import com.mtcarpenter.provider.member.MemberProvider;
import com.mtcarpenter.provider.order.OrderDataProvider;
import com.mtcarpenter.provider.stock.StockProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class OrderGenerationServiceImpl implements OrderGenerationService {

    @Autowired private CartReadService cartItemService;
    @Autowired private CartWriteService cartWriteService;
    @Autowired private RedisService redisService;
    @Autowired private CouponProvider couponProvider;
    @Autowired private IntegrationProvider integrationProvider;
    @Autowired private MemberProvider memberProvider;
    @Autowired private StockProvider stockProvider;
    @Autowired private OrderDataProvider orderDataProvider;
    @Autowired private HttpServletRequest request;

    @Value("${redis.key.orderId}")
    private String REDIS_KEY_ORDER_ID;
    @Value("${redis.database}")
    private String REDIS_DATABASE;

    @Override
    public ConfirmOrderResult generateConfirmOrder(List<Long> cartIds) {
        UmsMember currentMember = memberProvider.getCurrentMember(request);
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), cartIds);

        ConfirmOrderResult result = new ConfirmOrderResult();
        result.setCartPromotionItemList(cartPromotionItemList);
        result.setMemberReceiveAddressList(memberProvider.listAddresses(currentMember.getId()));
        result.setCouponHistoryDetailList(couponProvider.listCartPromotion(cartPromotionItemList, currentMember.getId()));
        result.setMemberIntegration(currentMember.getIntegration());
        result.setIntegrationConsumeSetting(integrationProvider.getConsumeSetting());
        result.setCalcAmount(calcCartAmount(cartPromotionItemList));
        return result;
    }

    @Override
    public Map<String, Object> generateOrder(OrderParam orderParam) {
        UmsMember currentMember = memberProvider.getCurrentMember(request);
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), orderParam.getCartIds());

        List<OmsOrderItem> orderItemList = buildOrderItems(cartPromotionItemList);
        if (!stockProvider.hasStock(cartPromotionItemList)) Asserts.fail("库存不足，无法下单");

        // Apply coupon
        if (orderParam.getCouponId() != null) {
            SmsCouponHistoryDetail coupon = couponProvider.getUseCoupon(cartPromotionItemList, orderParam.getCouponId(), currentMember.getId());
            if (coupon == null) Asserts.fail("该优惠券不可用");
            couponProvider.handleCouponAmount(orderItemList, coupon);
        } else {
            orderItemList.forEach(item -> item.setCouponAmount(BigDecimal.ZERO));
        }

        // Apply integration
        if (orderParam.getUseIntegration() != null && orderParam.getUseIntegration() > 0) {
            BigDecimal totalAmount = calcTotalAmount(orderItemList);
            BigDecimal integrationAmount = integrationProvider.getUseIntegrationAmount(orderParam.getUseIntegration(), totalAmount, currentMember, orderParam.getCouponId() != null);
            if (integrationAmount.compareTo(BigDecimal.ZERO) == 0) {
                Asserts.fail("积分不可用");
            } else {
                for (OmsOrderItem item : orderItemList) {
                    BigDecimal perAmount = item.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(integrationAmount);
                    item.setIntegrationAmount(perAmount);
                }
            }
        } else {
            orderItemList.forEach(item -> item.setIntegrationAmount(BigDecimal.ZERO));
        }

        handleRealAmount(orderItemList);
        stockProvider.lockStock(cartPromotionItemList);

        OmsOrder order = buildOrderEntity(orderParam, orderItemList, currentMember);
        orderDataProvider.insertOrder(order, orderItemList);

        if (orderParam.getCouponId() != null) couponProvider.updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        if (orderParam.getUseIntegration() != null) integrationProvider.updateIntegration(currentMember.getId(), -orderParam.getUseIntegration());

        deleteCartItemList(cartPromotionItemList, currentMember);

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return result;
    }

    private List<OmsOrderItem> buildOrderItems(List<CartPromotionItem> items) {
        List<OmsOrderItem> list = new ArrayList<>();
        for (CartPromotionItem c : items) {
            OmsOrderItem o = new OmsOrderItem();
            o.setProductId(c.getProductId());
            o.setProductName(c.getProductName());
            o.setProductPic(c.getProductPic());
            o.setProductAttr(c.getProductAttr());
            o.setProductBrand(c.getProductBrand());
            o.setProductSn(c.getProductSn());
            o.setProductPrice(c.getPrice());
            o.setProductQuantity(c.getQuantity());
            o.setProductSkuId(c.getProductSkuId());
            o.setProductSkuCode(c.getProductSkuCode());
            o.setProductCategoryId(c.getProductCategoryId());
            o.setPromotionAmount(c.getReduceAmount());
            o.setPromotionName(c.getPromotionMessage());
            o.setGiftIntegration(c.getIntegration());
            o.setGiftGrowth(c.getGrowth());
            list.add(o);
        }
        return list;
    }

    private OmsOrder buildOrderEntity(OrderParam orderParam, List<OmsOrderItem> orderItemList, UmsMember currentMember) {
        OmsOrder order = new OmsOrder();
        order.setMemberId(currentMember.getId());
        order.setMemberUsername(currentMember.getUsername());
        order.setCreateTime(new Date());
        order.setStatus(0);
        order.setOrderType(0);
        order.setSourceType(1);
        order.setPayType(orderParam.getPayType());

        UmsMemberReceiveAddress address = memberProvider.getAddress(orderParam.getMemberReceiveAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        order.setIntegration(calcGifIntegration(orderItemList));
        order.setGrowth(calcGiftGrowth(orderItemList));
        order.setOrderSn(generateOrderSn(order));

        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        order.setCouponAmount(orderParam.getCouponId() == null ? BigDecimal.ZERO : calcCouponAmount(orderItemList));
        order.setCouponId(orderParam.getCouponId());
        order.setIntegrationAmount(integrationProvider.calcIntegrationAmount(orderItemList));
        order.setPayAmount(calcPayAmount(order));
        return order;
    }

    private void deleteCartItemList(List<CartPromotionItem> cartPromotionItemList, UmsMember currentMember) {
        List<Long> ids = new ArrayList<>();
        for (CartPromotionItem item : cartPromotionItemList) ids.add(item.getId());
        cartWriteService.delete(currentMember.getId(), ids);
    }

    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList) {
        return orderItemList.stream().mapToInt(i -> i.getGiftGrowth() * i.getProductQuantity()).sum();
    }

    private Integer calcGifIntegration(List<OmsOrderItem> orderItemList) {
        return orderItemList.stream().mapToInt(i -> i.getGiftIntegration() * i.getProductQuantity()).sum();
    }

    private void handleRealAmount(List<OmsOrderItem> items) {
        for (OmsOrderItem item : items) {
            BigDecimal realAmount = item.getProductPrice()
                    .subtract(item.getPromotionAmount())
                    .subtract(item.getCouponAmount())
                    .subtract(item.getIntegrationAmount());
            item.setRealAmount(realAmount);
        }
    }

    private String getOrderPromotionInfo(List<OmsOrderItem> items) {
        return items.stream().map(OmsOrderItem::getPromotionName).reduce((a, b) -> a + ";" + b).orElse("");
    }

    private BigDecimal calcPayAmount(OmsOrder order) {
        return order.getTotalAmount()
                .add(order.getFreightAmount())
                .subtract(order.getPromotionAmount())
                .subtract(order.getCouponAmount())
                .subtract(order.getIntegrationAmount());
    }

    private BigDecimal calcCouponAmount(List<OmsOrderItem> items) {
        return items.stream()
                .filter(i -> i.getCouponAmount() != null)
                .map(i -> i.getCouponAmount().multiply(BigDecimal.valueOf(i.getProductQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcPromotionAmount(List<OmsOrderItem> items) {
        return items.stream()
                .filter(i -> i.getPromotionAmount() != null)
                .map(i -> i.getPromotionAmount().multiply(BigDecimal.valueOf(i.getProductQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcTotalAmount(List<OmsOrderItem> items) {
        return items.stream()
                .map(i -> i.getProductPrice().multiply(BigDecimal.valueOf(i.getProductQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartPromotionItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal promotion = BigDecimal.ZERO;
        for (CartPromotionItem i : items) {
            total = total.add(i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
            promotion = promotion.add(i.getReduceAmount().multiply(BigDecimal.valueOf(i.getQuantity())));
        }
        ConfirmOrderResult.CalcAmount amount = new ConfirmOrderResult.CalcAmount();
        amount.setTotalAmount(total);
        amount.setPromotionAmount(promotion);
        amount.setFreightAmount(BigDecimal.ZERO);
        amount.setPayAmount(total.subtract(promotion));
        return amount;
    }

    private String generateOrderSn(OmsOrder order) {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ORDER_ID + date;
        Long increment = redisService.incr(key, 1);
        return date + String.format("%02d", order.getSourceType()) +
                String.format("%02d", order.getPayType()) +
                String.format("%06d", increment);
    }
}
