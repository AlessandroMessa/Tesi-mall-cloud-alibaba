package com.mtcarpenter.mall.client.product.query;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.domain.CartProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mall-portal-product", path = "product")
public interface CartProductQueryClient {

    @GetMapping("/getProduct/{productId}")
    CommonResult<CartProduct> getCartProduct(@PathVariable("productId") Long productId);
}
