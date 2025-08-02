package com.mtcarpenter.mall.client.member.command;

import com.mtcarpenter.mall.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "mall-portal-member")
public interface MemberIntegrationCommandClient {
    /**
     * 根据会员id修改会员积分
     *
     * @param id
     * @param integration
     * @return
     */
    @RequestMapping(value = "/sso/updateIntegration", method = RequestMethod.GET)
    CommonResult updateIntegration(@RequestParam("id") Long id, @RequestParam("integration") Integer integration);
}
