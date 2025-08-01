package com.mtcarpenter.mall.client.cms.query;

import com.mtcarpenter.mall.common.CmsPrefrenceAreaProductRelationInput;
import com.mtcarpenter.mall.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-admin-cms", path = "prefrenceArea")
public interface CmsRelationQueryClient {

    @GetMapping("/relationByProductId")
    CommonResult<List<CmsPrefrenceAreaProductRelationInput>> relationByProductId(
            @RequestParam("productId") Long productId
    );
}
