package com.mtcarpenter.mall.client.cms.command;

import com.mtcarpenter.mall.common.CmsSubjectProductRelationInput;
import com.mtcarpenter.mall.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-admin-cms", path = "subject")
public interface CmsSubjectCommandClient {

    @PostMapping("/relateAndInsertList")
    CommonResult relateAndInsertList(
            @RequestBody List<CmsSubjectProductRelationInput> inputs,
            @RequestParam("productId") Long productId
    );

    @PostMapping("/relateAndUpdateList")
    CommonResult relateAndUpdateList(
            @RequestBody List<CmsSubjectProductRelationInput> inputs,
            @RequestParam("productId") Long productId
    );
}
