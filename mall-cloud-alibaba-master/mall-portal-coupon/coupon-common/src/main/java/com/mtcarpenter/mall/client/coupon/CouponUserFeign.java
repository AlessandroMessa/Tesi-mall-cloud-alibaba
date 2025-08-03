
package com.mtcarpenter.mall.client.coupon;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.model.SmsCoupon;
import com.mtcarpenter.mall.model.SmsCouponHistory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-portal-coupon", path = "coupon/user")
public interface CouponUserFeign {
    @PostMapping("/add/{couponId}")
    CommonResult add(@PathVariable Long couponId, @RequestParam("memberId") Long memberId, @RequestParam("nickName") String nickName);

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
    /**
     * 商品可用优惠券
     *
     * @param productId
     * @param productCategoryId
     * @return
     */
    @RequestMapping(value = "/getAvailableCouponList", method = RequestMethod.GET)
    CommonResult<List<SmsCoupon>> getAvailableCouponList(@RequestParam(value = "productId") Long productId,
                                                         @RequestParam(value = "productCategoryId") Long productCategoryId);


}
