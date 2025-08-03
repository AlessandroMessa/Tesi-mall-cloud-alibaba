package com.mtcarpenter.provider.cart;

import com.mtcarpenter.mall.client.product.query.cart.CartProductQueryClient;
import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.common.api.ResultCode;
import com.mtcarpenter.mall.domain.cart.CartProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductQueryProvider {

    @Autowired
    private CartProductQueryClient cartProductQueryClient;

    public CartProduct getCartProduct(Long productId) {
        CommonResult<CartProduct> result = cartProductQueryClient.getCartProduct(productId);
        if (result.getCode() == ResultCode.SUCCESS.getCode()) {
            return result.getData();
        }
        return null;
    }
}
