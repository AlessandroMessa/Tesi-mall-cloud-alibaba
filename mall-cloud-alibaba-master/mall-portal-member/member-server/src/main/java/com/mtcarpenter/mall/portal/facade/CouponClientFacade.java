package com.mtcarpenter.mall.portal.facade;

import com.mtcarpenter.mall.client.cart.CartPromotionFeign;
import com.mtcarpenter.mall.client.coupon.CouponUserFeign;
import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.common.api.ResultCode;
import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.domain.SmsCouponHistoryDetail;
import com.mtcarpenter.mall.model.SmsCoupon;
import com.mtcarpenter.mall.model.SmsCouponHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponClientFacade {

    @Autowired
    private CouponUserFeign couponFeign;

    @Autowired
    private CartPromotionFeign cartPromotionFeign;

    public void addCoupon(Long couponId, Long memberId, String nickName) {
        CommonResult<?> result = couponFeign.add(couponId, memberId, nickName);
        extractData(result, "Errore nell'aggiunta del coupon");
    }

    public List<SmsCouponHistory> listCouponHistory(Long memberId, Integer useStatus) {
        return extractData(
                couponFeign.listHistory(memberId, useStatus),
                "Errore nel recupero della cronologia dei coupon"
        );
    }

    public List<SmsCoupon> listCoupons(Long memberId, Integer useStatus) {
        return extractData(
                couponFeign.list(memberId, useStatus),
                "Errore nel recupero dei coupon"
        );
    }

    public void updateCouponStatus(Long couponId, Long memberId, Integer useStatus) {
        CommonResult<?> result = couponFeign.updateCouponStatus(couponId, memberId, useStatus);
        extractData(result, "Errore nell'aggiornamento dello stato del coupon");
    }

    public List<SmsCoupon> getAvailableCouponList(Long productId, Long productCategoryId) {
        return extractData(
                couponFeign.getAvailableCouponList(productId, productCategoryId),
                "Errore nel recupero dei coupon disponibili"
        );
    }

    public List<SmsCouponHistoryDetail> listCartCoupons(Integer type, Long memberId) {
        return extractData(
                cartPromotionFeign.listCart(type, memberId),
                "Errore nel recupero dei coupon per il carrello"
        );
    }

    public List<SmsCouponHistoryDetail> listCartPromotion(Integer type, List<CartPromotionItem> cartPromotionItemList, Long memberId) {
        return extractData(
                cartPromotionFeign.listCartPromotion(type, cartPromotionItemList, memberId),
                "Errore nel recupero delle promozioni del carrello"
        );
    }

    // ✅ Metodo centralizzato per estrarre e validare i CommonResult
    private <T> T extractData(CommonResult<T> result, String errorMessage) {
        if (result == null || result.getCode() == ResultCode.SUCCESS.getCode()) {
            throw new RuntimeException(errorMessage + (result != null ? ": " + result.getMessage() : ""));
        }
        return result.getData();
    }
}
