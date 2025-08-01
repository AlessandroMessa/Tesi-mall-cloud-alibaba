package com.mtcarpenter.mall.client.promotion;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.model.promotion.SmsFlashPromotion;
import com.mtcarpenter.mall.model.promotion.SmsFlashPromotionSession;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@FeignClient(name = "mall-portal-coupon", path = "coupon")
public interface FlashPromotionClient {

    @GetMapping("/getNextFlashPromotionSession")
    CommonResult<SmsFlashPromotionSession> getNextFlashPromotionSession(@RequestParam("date") Date date);

    @GetMapping("/getFlashPromotion")
    CommonResult<SmsFlashPromotion> getFlashPromotion(@RequestParam("date") Date date);

    @GetMapping("/getFlashPromotionSession")
    CommonResult<SmsFlashPromotionSession> getFlashPromotionSession(@RequestParam("date") Date date);
}
