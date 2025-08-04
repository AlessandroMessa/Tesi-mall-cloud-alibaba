package com.mtcarpenter.mall.service.base;

import com.mtcarpenter.mall.model.auth.UmsPermission;
import com.mtcarpenter.mall.model.auth.UmsRole;
import com.mtcarpenter.mall.model.auth.resource.UmsResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AdminRoleService {
    @Transactional
    int updateRole(Long adminId, List<Long> roleIds);

    /**
     * 获取用户对于角色
     */
    List<UmsRole> getRoleList(Long adminId);



    /**
     * 修改用户的+-权限
     */
    @Transactional
    int updatePermission(Long adminId, List<Long> permissionIds);

    /**
     * 获取用户所有权限（包括角色权限和+-权限）
     */
    List<UmsPermission> getPermissionList(Long adminId);

}
