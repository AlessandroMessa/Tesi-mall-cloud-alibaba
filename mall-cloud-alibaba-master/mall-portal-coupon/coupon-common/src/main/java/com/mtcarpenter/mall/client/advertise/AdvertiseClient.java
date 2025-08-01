package com.mtcarpenter.mall.client.advertise;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.model.home.SmsHomeAdvertise;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-portal-coupon", path = "coupon")
public interface AdvertiseClient {

    @GetMapping("/getHomeAdvertiseList")
    CommonResult<List<SmsHomeAdvertise>> getHomeAdvertiseList();
}
