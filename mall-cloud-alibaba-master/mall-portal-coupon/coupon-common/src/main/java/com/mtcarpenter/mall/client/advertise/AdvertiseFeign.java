package com.mtcarpenter.mall.client.advertise;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.model.SmsHomeAdvertise;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "mall-portal-coupon", path = "coupon/advertise")
public interface AdvertiseFeign {
    @GetMapping("/getHomeAdvertiseList")
    CommonResult<List<SmsHomeAdvertise>> getHomeAdvertiseList();
}
