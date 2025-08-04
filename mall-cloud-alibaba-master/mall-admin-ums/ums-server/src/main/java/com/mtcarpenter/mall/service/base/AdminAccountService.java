package com.mtcarpenter.mall.service.base;

import com.mtcarpenter.mall.dto.UmsAdminParam;
import com.mtcarpenter.mall.dto.UpdateAdminPasswordParam;
import com.mtcarpenter.mall.model.admin.UmsAdmin;

import java.util.List;

public interface AdminAccountService {

    /**
     * 注册功能
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);

    /**
     * 根据用户id获取用户
     */
    UmsAdmin getItem(Long id);

    /**
     * 根据用户名或昵称分页查询用户
     */
    List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum);
    /**
     * 修改指定用户信息
     */
    int update(Long id, UmsAdmin admin);

    /**
     * 删除指定用户
     */
    int delete(Long id);

    /**
     * 修改密码
     */
    int updatePassword(UpdateAdminPasswordParam updatePasswordParam);

}
