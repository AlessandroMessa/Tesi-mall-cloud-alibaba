package com.mtcarpenter.mall.client.cms.query;

import com.mtcarpenter.mall.common.CmsSubjectProductRelationInput;
import com.mtcarpenter.mall.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-admin-cms", path = "subject")
public interface CmsSubjectQueryClient {

    @GetMapping("/relationByProductId")
    CommonResult<List<CmsSubjectProductRelationInput>> relationByProductId(
            @RequestParam("productId") Long productId
    );
}
