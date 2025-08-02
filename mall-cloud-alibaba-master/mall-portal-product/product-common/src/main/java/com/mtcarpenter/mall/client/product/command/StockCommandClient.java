package com.mtcarpenter.mall.client.product.command;

import com.mtcarpenter.mall.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mall-portal-product", path = "product")
public interface StockCommandClient {

    @PostMapping("/lockStock")
    CommonResult lockStock(@RequestParam(required = false) Long productSkuId,
                           @RequestParam(required = false) Integer quantity);
}
