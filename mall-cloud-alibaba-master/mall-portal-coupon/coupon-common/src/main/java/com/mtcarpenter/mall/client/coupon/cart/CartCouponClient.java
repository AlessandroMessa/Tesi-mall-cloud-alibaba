package com.mtcarpenter.mall.client.coupon.cart;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.domain.SmsCouponHistoryDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-portal-coupon", path = "coupon")
public interface CartCouponClient {

    @GetMapping("/list/cart/{type}")
    CommonResult<List<SmsCouponHistoryDetail>> listCart(@PathVariable("type") Integer type,
                                                        @RequestParam(value = "memberId", required = false) Long memberId);

    @PostMapping("/list/cart/{type}")
    CommonResult<List<SmsCouponHistoryDetail>> listCartPromotion(@PathVariable("type") Integer type,
                                                                 @RequestBody List<CartPromotionItem> cartPromotionItemList,
                                                                 @RequestParam(value = "memberId", required = false) Long memberId);
}
