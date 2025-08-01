package com.mtcarpenter.mall.client.cms.command;

import com.mtcarpenter.mall.common.CmsPrefrenceAreaProductRelationInput;
import com.mtcarpenter.mall.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mall-admin-cms", path = "prefrenceArea")
public interface CmsRelationCommandClient {

    @PostMapping("/relateAndInsertList")
    CommonResult relateAndInsertList(
            @RequestBody List<CmsPrefrenceAreaProductRelationInput> inputs,
            @RequestParam("productId") Long productId
    );

    @PostMapping("/relateAndUpdateList")
    CommonResult relateAndUpdateList(
            @RequestBody List<CmsPrefrenceAreaProductRelationInput> inputs,
            @RequestParam("productId") Long productId
    );
}
