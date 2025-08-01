package com.mtcarpenter.mall.client.basic;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.model.coupon.SmsCoupon;
import com.mtcarpenter.mall.model.coupon.SmsCouponHistory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "mall-portal-coupon", path = "coupon")
public interface CouponBasicClient {

    @PostMapping("/add/{couponId}")
    CommonResult add(@PathVariable("couponId") Long couponId,
                     @RequestParam("memberId") Long memberId,
                     @RequestParam("nickName") String nickName);

    @GetMapping("/listHistory")
    CommonResult<List<SmsCouponHistory>> listHistory(@RequestParam(value = "memberId", required = false) Long memberId,
                                                     @RequestParam(value = "useStatus", required = false) Integer useStatus);

    @GetMapping("/list")
    CommonResult<List<SmsCoupon>> list(@RequestParam(value = "memberId", required = false) Long memberId,
                                       @RequestParam(value = "useStatus", required = false) Integer useStatus);

    @GetMapping("/updateCouponStatus")
    CommonResult updateCouponStatus(@RequestParam("couponId") Long couponId,
                                    @RequestParam("memberId") Long memberId,
                                    @RequestParam("useStatus") Integer useStatus);
}