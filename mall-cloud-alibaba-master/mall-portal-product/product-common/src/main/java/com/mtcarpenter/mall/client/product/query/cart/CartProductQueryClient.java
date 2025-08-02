package com.mtcarpenter.mall.client.product.query.cart;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.domain.cart.CartProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mall-portal-product", path = "product")
public interface CartProductQueryClient {

    @GetMapping("/getProduct/{productId}")
    CommonResult<CartProduct> getCartProduct(@PathVariable("productId") Long productId);
}
