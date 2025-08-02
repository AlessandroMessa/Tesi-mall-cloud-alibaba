package com.mtcarpenter.mall.client.coupon.command;

import com.mtcarpenter.mall.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "mall-portal-coupon")
public interface CouponCommandClient {
    @PostMapping("/add/{couponId}")
    CommonResult add(@PathVariable("couponId") Long couponId,
                     @RequestParam("memberId") Long memberId,
                     @RequestParam("nickName") String nickName);

    @GetMapping("/updateCouponStatus")
    CommonResult updateCouponStatus(@RequestParam("couponId") Long couponId,
                                    @RequestParam("memberId") Long memberId,
                                    @RequestParam("useStatus") Integer useStatus);
}
