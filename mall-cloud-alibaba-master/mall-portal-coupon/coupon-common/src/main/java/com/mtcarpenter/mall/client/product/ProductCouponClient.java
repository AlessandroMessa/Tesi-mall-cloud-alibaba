package com.mtcarpenter.mall.client.product;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.model.coupon.SmsCoupon;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-portal-coupon", path = "coupon")
public interface ProductCouponClient {

    @GetMapping("/getAvailableCouponList")
    CommonResult<List<SmsCoupon>> getAvailableCouponList(@RequestParam("productId") Long productId,
                                                         @RequestParam("productCategoryId") Long productCategoryId);
}
