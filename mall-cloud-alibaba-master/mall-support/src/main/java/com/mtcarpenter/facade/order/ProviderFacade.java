package com.mtcarpenter.facade.order;

import com.mtcarpenter.mall.client.coupon.cart.CartCouponClient;
import com.mtcarpenter.mall.client.coupon.command.CouponCommandClient;
import com.mtcarpenter.mall.client.integration.IntegrationSettingQueryClient;
import com.mtcarpenter.mall.client.member.command.MemberIntegrationCommandClient;
import com.mtcarpenter.mall.client.member.query.MemberAddressQueryClient;
import com.mtcarpenter.mall.client.product.command.StockCommandClient;
import com.mtcarpenter.mall.dao.PortalOrderDao;
import com.mtcarpenter.mall.dao.PortalOrderItemDao;
import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.domain.SmsCouponHistoryDetail;
import com.mtcarpenter.mall.domain.dto.OmsOrderDetail;
import com.mtcarpenter.mall.model.UmsIntegrationConsumeSetting;
import com.mtcarpenter.mall.model.UmsMember;
import com.mtcarpenter.mall.model.UmsMemberReceiveAddress;
import com.mtcarpenter.mall.model.coupon.SmsCoupon;
import com.mtcarpenter.mall.model.coupon.SmsCouponProductCategoryRelation;
import com.mtcarpenter.mall.model.coupon.SmsCouponProductRelation;
import com.mtcarpenter.mall.model.OmsOrder;
import com.mtcarpenter.mall.model.OmsOrderItem;
import com.mtcarpenter.mall.model.OmsOrderSetting;
import com.mtcarpenter.mall.model.order.OmsOrderExample;
import com.mtcarpenter.mall.model.order.OmsOrderItemExample;
import com.mtcarpenter.mall.util.MemberUtil;
import com.mtcarpenter.mall.mapper.OmsOrderItemMapper;
import com.mtcarpenter.mall.mapper.OmsOrderMapper;
import com.mtcarpenter.mall.mapper.OmsOrderSettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Unified facade aggregating coupon, integration, member, stock, and order operations.
 * Preserve original provider method signatures.
 */
@Service
public class ProviderFacade {

    @Autowired
    private CartCouponClient cartCouponClient;

    @Autowired
    private CouponCommandClient couponCommandClient;

    @Autowired
    private IntegrationSettingQueryClient integrationSettingQueryClient;

    @Autowired
    private MemberIntegrationCommandClient memberIntegrationCommandClient;

    @Autowired
    private MemberUtil memberUtil;

    @Autowired
    private MemberAddressQueryClient memberAddressQueryClient;

    @Autowired
    private StockCommandClient stockCommandClient;

    @Autowired
    private PortalOrderDao portalOrderDao;

    @Autowired
    private PortalOrderItemDao portalOrderItemDao;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;

    /**
     * listCartPromotion from CouponProvider
     */
    public List<SmsCouponHistoryDetail> listCartPromotion(List<CartPromotionItem> items, Long memberId) {
        return cartCouponClient.listCartPromotion(1, items, memberId).getData();
    }

    /**
     * getUseCoupon from CouponProvider
     */
    public SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> items, Long couponId, Long memberId) {
        return listCartPromotion(items, memberId).stream()
                .filter(c -> c.getCoupon().getId().equals(couponId))
                .findFirst()
                .orElse(null);
    }

    /**
     * updateCouponStatus from CouponProvider
     */
    public void updateCouponStatus(Long couponId, Long memberId, int status) {
        couponCommandClient.updateCouponStatus(couponId, memberId, status);
    }

    /**
     * handleCouponAmount from CouponProvider
     */
    public void handleCouponAmount(List<OmsOrderItem> orderItems, SmsCouponHistoryDetail detail) {
        SmsCoupon coupon = detail.getCoupon();
        if (coupon.getUseType().equals(0)) {
            calcPerCouponAmount(orderItems, coupon);
        } else if (coupon.getUseType().equals(1)) {
            List<OmsOrderItem> filtered = filterByCategory(detail, orderItems);
            calcPerCouponAmount(filtered, coupon);
        } else if (coupon.getUseType().equals(2)) {
            List<OmsOrderItem> filtered = filterByProduct(detail, orderItems);
            calcPerCouponAmount(filtered, coupon);
        }
    }

    private void calcPerCouponAmount(List<OmsOrderItem> items, SmsCoupon coupon) {
        BigDecimal total = items.stream()
                .map(i -> i.getProductPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        for (OmsOrderItem item : items) {
            BigDecimal amount = item.getProductPrice().divide(total, 3, RoundingMode.HALF_EVEN)
                    .multiply(coupon.getAmount());
            item.setCouponAmount(amount);
        }
    }

    private List<OmsOrderItem> filterByCategory(SmsCouponHistoryDetail detail, List<OmsOrderItem> items) {
        List<Long> ids = new ArrayList<>();
        for (SmsCouponProductCategoryRelation r : detail.getCategoryRelationList()) {
            ids.add(r.getProductCategoryId());
        }
        List<OmsOrderItem> result = new ArrayList<>();
        for (OmsOrderItem item : items) {
            if (ids.contains(item.getProductCategoryId())) {
                result.add(item);
            } else {
                item.setCouponAmount(BigDecimal.ZERO);
            }
        }
        return result;
    }

    private List<OmsOrderItem> filterByProduct(SmsCouponHistoryDetail detail, List<OmsOrderItem> items) {
        List<Long> ids = new ArrayList<>();
        for (SmsCouponProductRelation r : detail.getProductRelationList()) {
            ids.add(r.getProductId());
        }
        List<OmsOrderItem> result = new ArrayList<>();
        for (OmsOrderItem item : items) {
            if (ids.contains(item.getProductId())) {
                result.add(item);
            } else {
                item.setCouponAmount(BigDecimal.ZERO);
            }
        }
        return result;
    }

    /**
     * getConsumeSetting from IntegrationProvider
     */
    public UmsIntegrationConsumeSetting getConsumeSetting() {
        return integrationSettingQueryClient.integrationConsumeSetting(1L).getData();
    }

    /**
     * updateIntegration from IntegrationProvider
     */
    public void updateIntegration(Long memberId, int delta) {
        memberIntegrationCommandClient.updateIntegration(memberId, delta);
    }

    /**
     * getUseIntegrationAmount from IntegrationProvider
     */
    public BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UmsMember member, boolean hasCoupon) {
        if (useIntegration > member.getIntegration()) return BigDecimal.ZERO;

        UmsIntegrationConsumeSetting setting = getConsumeSetting();

        if (hasCoupon && setting.getCouponStatus().equals(0)) return BigDecimal.ZERO;
        if (useIntegration < setting.getUseUnit()) return BigDecimal.ZERO;

        BigDecimal integrationAmount = new BigDecimal(useIntegration)
                .divide(new BigDecimal(setting.getUseUnit()), 2, RoundingMode.HALF_EVEN);

        BigDecimal maxPercent = new BigDecimal(setting.getMaxPercentPerOrder())
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);

        return integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0
                ? BigDecimal.ZERO : integrationAmount;
    }

    /**
     * calcIntegrationAmount from IntegrationProvider
     */
    public BigDecimal calcIntegrationAmount(List<OmsOrderItem> items) {
        return items.stream()
                .filter(i -> i.getIntegrationAmount() != null)
                .map(i -> i.getIntegrationAmount().multiply(BigDecimal.valueOf(i.getProductQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * getCurrentMember from MemberProvider
     */
    public UmsMember getCurrentMember(HttpServletRequest request) {
        return memberUtil.getRedisUmsMember(request);
    }

    /**
     * listAddresses from MemberProvider
     */
    public List<UmsMemberReceiveAddress> listAddresses(Long memberId) {
        return memberAddressQueryClient.list(memberId).getData();
    }

    /**
     * getAddress from MemberProvider
     */
    public UmsMemberReceiveAddress getAddress(Long addressId) {
        return memberAddressQueryClient.getItem(addressId).getData();
    }

    /**
     * hasStock from StockProvider
     */
    public boolean hasStock(List<CartPromotionItem> items) {
        return items.stream().allMatch(i -> i.getRealStock() != null && i.getRealStock() > 0);
    }

    /**
     * lockStock from StockProvider
     */
    public void lockStock(List<CartPromotionItem> items) {
        for (CartPromotionItem item : items) {
            stockCommandClient.lockStock(item.getProductSkuId(), item.getQuantity());
        }
    }

    /**
     * releaseStock from StockProvider
     */
    public void releaseStock(List<OmsOrderItem> items) {
        portalOrderDao.releaseSkuStockLock(items);
    }

    /**
     * insertOrder from OrderDataProvider
     */
    public void insertOrder(OmsOrder order, List<OmsOrderItem> items) {
        orderMapper.insert(order);
        for (OmsOrderItem item : items) {
            item.setOrderId(order.getId());
            item.setOrderSn(order.getOrderSn());
        }
        portalOrderItemDao.insertList(items);
    }

    /**
     * getOrderSettings from OrderDataProvider
     */
    public List<OmsOrderSetting> getOrderSettings() {
        return orderSettingMapper.selectByExample(new com.mtcarpenter.mall.model.order.OmsOrderSettingExample());
    }

    /**
     * selectOrdersByExample from OrderDataProvider
     */
    public List<OmsOrder> selectOrdersByExample(OmsOrderExample example) {
        return orderMapper.selectByExample(example);
    }

    /**
     * selectOrderItemsByExample from OrderDataProvider
     */
    public List<OmsOrderItem> selectOrderItemsByExample(OmsOrderItemExample example) {
        return orderItemMapper.selectByExample(example);
    }

    /**
     * selectOrderById from OrderDataProvider
     */
    public OmsOrder selectOrderById(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * updateOrder from OrderDataProvider
     */
    public void updateOrder(OmsOrder order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * getOrderDetail from OrderDataProvider
     */
    public OmsOrderDetail getOrderDetail(Long orderId) {
        return portalOrderDao.getDetail(orderId);
    }

    public int updateSkuStock(List<OmsOrderItem> items) {
        return portalOrderDao.updateSkuStock(items);
    }
}
