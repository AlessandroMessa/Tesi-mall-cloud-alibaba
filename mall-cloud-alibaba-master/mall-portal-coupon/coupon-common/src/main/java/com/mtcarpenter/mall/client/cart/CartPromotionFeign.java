package com.mtcarpenter.mall.client.cart;
import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.domain.SmsCouponHistoryDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "mall-portal-coupon", path = "coupon/cart")
public interface CartPromotionFeign {
    @GetMapping("/list/{type}")
    CommonResult<List<SmsCouponHistoryDetail>> listCart(@PathVariable Integer type,
                                                        @RequestParam(value = "memberId", required = false) Long memberId);

    @PostMapping("/list/{type}")
    CommonResult<List<SmsCouponHistoryDetail>> listCartPromotion(@PathVariable Integer type,
                                                                 List<CartPromotionItem> cartPromotionItemList,
                                                                 @RequestParam(value = "memberId", required = false) Long memberId);
}
