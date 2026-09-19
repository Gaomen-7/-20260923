package com.gec.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.PermissionMapper;
import com.gec.dao.RoleMapper;
import com.gec.domain.entity.Permission;
import com.gec.domain.entity.Role;
import com.gec.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl
    extends ServiceImpl<RoleMapper, Role>
    implements IRoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public void addUserRoleAssociation(Integer userId, Integer roleId) {
        removeUserRoleAssociation(userId);
        int cnt = roleMapper.addUserRoleAssociation(userId, roleId);
        if (cnt != 1) {
            throw new RuntimeException("设置用户-角色关联失败");
        }
    }

    @Override
    public void removeUserRoleAssociation(Integer userId) {
        roleMapper.removeUserRoleAssociation(userId);
    }

    @Override
    public IPage<Role> listRole(Page page, Map<String, Object> data) {
        String roleName = data != null ? (String) data.get("roleName") : null;
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Role> qw =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (roleName != null && !roleName.isEmpty()) {
            qw.like("role_name", roleName);
        }
        qw.orderByAsc("id");
        return page(page, qw);
    }

    @Override
    @Transactional
    public void deleteRole(Integer id) {
        // 禁止删除超级管理员(id=1)
        if (id != null && id == 1) {
            throw new RuntimeException("超级管理员角色不允许删除");
        }
        // 删角色前先删权限关联
        permissionMapper.deleteByRoleId(id);
        // 删角色
        removeById(id);
    }

    @Override
    public void addRole(Role role) {
        save(role);
    }

    @Override
    public void updateRole(Role role) {
        updateById(role);
    }

    @Override
    public Role getRole(Integer id) {
        return getById(id);
    }

    /** 查询全部权限点 */
    @Override
    public List<Permission> listAllPermissions() {
        return permissionMapper.selectList(null);
    }

    /** 查询某角色已分配的权限ID列表 */
    @Override
    public List<Integer> getRolePermissionIds(Integer roleId) {
        return permissionMapper.findPermissionIdsByRoleId(roleId);
    }

    /** 给角色分配权限（先删后插） */
    @Override
    @Transactional
    public void assignPermissions(Integer roleId, List<Integer> permissionIds) {
        permissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            permissionMapper.batchInsertRolePermission(roleId, permissionIds);
        }
    }
}
