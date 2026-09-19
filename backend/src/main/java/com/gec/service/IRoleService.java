package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.Role;

import java.util.Map;

public interface IRoleService extends IService<Role> {

    void addUserRoleAssociation(
            Integer userId, Integer roleId);
    void removeUserRoleAssociation(
            Integer userId);

    IPage<Role> listRole(
            Page page, Map<String, Object> data);

    void deleteRole(Integer id);

    void addRole(Role role);

    void updateRole(Role role);

    Role getRole(Integer id);

    /** 查询全部权限点（平铺） */
    java.util.List<com.gec.domain.entity.Permission> listAllPermissions();

    /** 查询某角色已分配的权限ID列表 */
    java.util.List<Integer> getRolePermissionIds(Integer roleId);

    /** 给角色分配权限（先删后插，事务） */
    void assignPermissions(Integer roleId, java.util.List<Integer> permissionIds);

}



