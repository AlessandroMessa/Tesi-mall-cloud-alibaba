package com.mtcarpenter.mall.client.product.query;

import com.mtcarpenter.mall.common.api.CommonResult;
import com.mtcarpenter.mall.domain.PromotionProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-portal-product", path = "product")
public interface PromotionProductQueryClient {

    @PostMapping("/getPromotionProductList")
    CommonResult<List<PromotionProduct>> getPromotionProductList(@RequestBody(required = false) List<Long> productIdList);
}
