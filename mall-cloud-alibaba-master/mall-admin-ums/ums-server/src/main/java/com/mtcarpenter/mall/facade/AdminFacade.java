package com.mtcarpenter.mall.facade;

import com.mtcarpenter.mall.dto.UmsAdminParam;
import com.mtcarpenter.mall.dto.UpdateAdminPasswordParam;
import com.mtcarpenter.mall.model.admin.UmsAdmin;
import com.mtcarpenter.mall.model.auth.UmsPermission;
import com.mtcarpenter.mall.model.auth.UmsRole;
import com.mtcarpenter.mall.model.auth.resource.UmsResource;

import java.util.List;

public interface AdminFacade {
    String login(String username, String password);
    String refreshToken(String oldToken);
    UmsAdmin register(UmsAdminParam umsAdminParam);
    UmsAdmin getAdminByUsername(String username);
    UmsAdmin getItem(Long id);
    List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum);
    int update(Long id, UmsAdmin admin);
    int delete(Long id);
    int updatePassword(UpdateAdminPasswordParam param);
    List<UmsRole> getRoleList(Long adminId);
    int updateRole(Long adminId, List<Long> roleIds);
    List<UmsPermission> getPermissionList(Long adminId);
    int updatePermission(Long adminId, List<Long> permissionIds);
    List<UmsResource> getResourceList(Long adminId);
}
