package com.mtcarpenter.provider;

import com.mtcarpenter.mall.client.coupon.cart.CartCouponClient;
import com.mtcarpenter.mall.client.coupon.command.CouponCommandClient;
import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.domain.SmsCouponHistoryDetail;
import com.mtcarpenter.mall.model.OmsOrderItem;
import com.mtcarpenter.mall.model.coupon.SmsCoupon;
import com.mtcarpenter.mall.model.coupon.SmsCouponProductCategoryRelation;
import com.mtcarpenter.mall.model.coupon.SmsCouponProductRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponProvider {

    @Autowired
    private CartCouponClient cartCouponClient;
    @Autowired
    private CouponCommandClient couponCommandClient;

    public List<SmsCouponHistoryDetail> listCartPromotion(List<CartPromotionItem> items, Long memberId) {
        return cartCouponClient.listCartPromotion(1, items, memberId).getData();
    }

    public SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> items, Long couponId, Long memberId) {
        return listCartPromotion(items, memberId).stream()
                .filter(c -> c.getCoupon().getId().equals(couponId))
                .findFirst()
                .orElse(null);
    }

    public void updateCouponStatus(Long couponId, Long memberId, int status) {
        couponCommandClient.updateCouponStatus(couponId, memberId, status);
    }

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
            BigDecimal amount = item.getProductPrice().divide(total, 3, RoundingMode.HALF_EVEN).multiply(coupon.getAmount());
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
}
