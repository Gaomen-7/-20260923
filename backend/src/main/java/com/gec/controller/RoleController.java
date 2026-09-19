package com.gec.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.dao.OptionMapper;
import com.gec.domain.entity.Role;
import com.gec.domain.vo.OptionVO;
import com.gec.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Role")
public class RoleController extends BaseController {

    @Autowired
    private OptionMapper optionMapper;
    @Autowired
    private IRoleService roleService;

    /* 角色下拉选项（供用户管理页使用） */
    @GetMapping("/roleOptions")
    public R roleOptions() {
        List<OptionVO> ops = optionMapper.roleOptions();
        return R.ok(ops);
    }

    /* 1.角色分页列表 */
    @PostMapping("/list/{page}/{limit}")
    public R list(@PathVariable("page") Integer page,
                  @PathVariable("limit") Integer limit,
                  @RequestBody(required = false) Map<String, Object> param) {
        Page frmPage = newPage(page, limit);
        IPage retPage = roleService.listRole(frmPage, param);
        return R.convertPage(retPage);
    }

    /* 2.添加角色 */
    @PostMapping("/add")
    public R add(@RequestBody Role role) {
        try {
            roleService.addRole(role);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.err(e);
        }
    }

    /* 3.更新角色 */
    @PutMapping("/update")
    public R update(@RequestBody Role role) {
        try {
            roleService.updateRole(role);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.err(e);
        }
    }

    /* 4.删除角色 */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id") Integer id) {
        try {
            roleService.deleteRole(id);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.err(e);
        }
    }

    /* 5.角色详情（含已分配权限ID列表） */
    @GetMapping("/getRole/{id}")
    public R getRole(@PathVariable("id") Integer id) {
        Role role = roleService.getRole(id);
        List<Integer> permIds = roleService.getRolePermissionIds(id);
        Map<String, Object> ret = new HashMap<>();
        ret.put("role", role);
        ret.put("permissionIds", permIds);
        return R.ok(ret);
    }

    /* 6.全部权限点列表（供权限分配树使用） */
    @GetMapping("/permissions")
    public R permissions() {
        return R.ok(roleService.listAllPermissions());
    }

    /* 7.给角色分配权限 */
    @PostMapping("/assignPermissions")
    public R assignPermissions(@RequestBody Map<String, Object> body) {
        Integer roleId = (Integer) body.get("roleId");
        @SuppressWarnings("unchecked")
        List<Integer> permissionIds = (List<Integer>) body.get("permissionIds");
        if (roleId == null) {
            return R.err(new RuntimeException("roleId 不能为空"));
        }
        roleService.assignPermissions(roleId, permissionIds);
        return R.ok();
    }

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }
}
