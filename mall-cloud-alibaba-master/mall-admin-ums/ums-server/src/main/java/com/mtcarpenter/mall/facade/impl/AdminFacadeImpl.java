package com.mtcarpenter.mall.facade.impl;

import com.mtcarpenter.mall.dto.UmsAdminParam;
import com.mtcarpenter.mall.dto.UpdateAdminPasswordParam;
import com.mtcarpenter.mall.facade.AdminFacade;
import com.mtcarpenter.mall.model.admin.UmsAdmin;
import com.mtcarpenter.mall.model.auth.UmsPermission;
import com.mtcarpenter.mall.model.auth.UmsRole;
import com.mtcarpenter.mall.model.auth.resource.UmsResource;
import com.mtcarpenter.mall.service.base.AdminAccountService;
import com.mtcarpenter.mall.service.base.AdminAuthService;
import com.mtcarpenter.mall.service.base.AdminRoleService;
import java.util.List;


public class AdminFacadeImpl implements AdminFacade {
    private final AdminAuthService authService;
    private final AdminAccountService accountService;
    private final AdminRoleService roleService;

    public AdminFacadeImpl(AdminAuthService authService,
                           AdminAccountService accountService,
                           AdminRoleService roleService) {
        this.authService = authService;
        this.accountService = accountService;
        this.roleService = roleService;
    }
    @Override
    public String login(String username, String password) {
        return authService.login(username, password);
    }

    @Override
    public String refreshToken(String oldToken) {
        return authService.refreshToken(oldToken);
    }

    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        return accountService.register(umsAdminParam);
    }

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        return authService.getAdminByUsername(username);
    }

    @Override
    public UmsAdmin getItem(Long id) {
        return accountService.getItem(id);
    }

    @Override
    public List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum) {
        return accountService.list(keyword, pageSize, pageNum);
    }

    @Override
    public int update(Long id, UmsAdmin admin) {
        return accountService.update(id, admin);
    }

    @Override
    public int delete(Long id) {
        return accountService.delete(id);
    }

    @Override
    public int updatePassword(UpdateAdminPasswordParam param) {
        return accountService.updatePassword(param);
    }

    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        return roleService.getRoleList(adminId);
    }

    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        return roleService.updateRole(adminId, roleIds);
    }

    @Override
    public List<UmsPermission> getPermissionList(Long adminId) {
        return roleService.getPermissionList(adminId);
    }

    @Override
    public int updatePermission(Long adminId, List<Long> permissionIds) {
        return roleService.updatePermission(adminId, permissionIds);
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        return authService.getResourceList(adminId);
    }
}
