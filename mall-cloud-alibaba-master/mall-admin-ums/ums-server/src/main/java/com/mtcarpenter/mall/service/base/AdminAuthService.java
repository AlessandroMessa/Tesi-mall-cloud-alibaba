package com.mtcarpenter.mall.service.base;

import com.mtcarpenter.mall.model.admin.UmsAdmin;
import com.mtcarpenter.mall.model.auth.resource.UmsResource;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface AdminAuthService {
    /**
     * 登录功能
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT的token
     */
    String login(String username, String password);

    /**
     * 刷新token的功能
     * @param oldToken 旧的token
     */
    String refreshToken(String oldToken);
    /**
     * 获取用户信息
     */
    UserDetails loadUserByUsername(String username);
    /**
     * 根据用户名获取后台管理员
     */
    UmsAdmin getAdminByUsername(String username);
    /**
     * 获取指定用户的可访问资源
     */
    List<UmsResource> getResourceList(Long adminId);

}
