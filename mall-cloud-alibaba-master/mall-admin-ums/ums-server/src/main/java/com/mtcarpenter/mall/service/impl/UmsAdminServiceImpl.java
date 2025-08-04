package com.mtcarpenter.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.mtcarpenter.mall.bo.AdminUserDetails;
import com.mtcarpenter.mall.dao.UmsAdminPermissionRelationDao;
import com.mtcarpenter.mall.dao.UmsAdminRoleRelationDao;
import com.mtcarpenter.mall.dto.UmsAdminParam;
import com.mtcarpenter.mall.dto.UpdateAdminPasswordParam;
import com.mtcarpenter.mall.mapper.UmsAdminLoginLogMapper;
import com.mtcarpenter.mall.mapper.UmsAdminMapper;
import com.mtcarpenter.mall.mapper.UmsAdminPermissionRelationMapper;
import com.mtcarpenter.mall.mapper.UmsAdminRoleRelationMapper;
import com.mtcarpenter.mall.model.admin.UmsAdmin;
import com.mtcarpenter.mall.model.admin.UmsAdminExample;
import com.mtcarpenter.mall.model.admin.login.UmsAdminLoginLog;
import com.mtcarpenter.mall.model.admin.permission.UmsAdminPermissionRelation;
import com.mtcarpenter.mall.model.admin.permission.UmsAdminPermissionRelationExample;
import com.mtcarpenter.mall.model.admin.role.UmsAdminRoleRelation;
import com.mtcarpenter.mall.model.admin.role.UmsAdminRoleRelationExample;
import com.mtcarpenter.mall.model.auth.UmsPermission;
import com.mtcarpenter.mall.model.auth.UmsRole;
import com.mtcarpenter.mall.model.auth.resource.UmsResource;
import com.mtcarpenter.mall.security.util.JwtTokenUtil;
import com.mtcarpenter.mall.service.UmsAdminCacheService;
import com.mtcarpenter.mall.service.UmsAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UmsAdminService实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsAdminServiceImpl implements UmsAdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;



}
