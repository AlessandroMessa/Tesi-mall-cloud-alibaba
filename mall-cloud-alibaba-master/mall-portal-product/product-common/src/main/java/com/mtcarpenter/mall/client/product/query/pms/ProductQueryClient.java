package com.mtcarpenter.mall.client.product.query.pms;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.model.PmsProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mall-portal-product", path = "product")
public interface ProductQueryClient {

    @GetMapping("/getPmsProductById/{productId}")
    CommonResult<PmsProduct> getPmsProductById(@PathVariable("productId") Long productId);
}
