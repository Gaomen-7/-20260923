import service from '@/network/request.js'

/* 获取角色下拉选项（用户管理页用） */
export function roleOptions(){
    return service({
        url: '/Role/roleOptions',
        method: 'GET'
    });
}

/* 角色分页列表 */
export function listRole(page, limit, param){
    return service({
        url: `/Role/list/${page}/${limit}`,
        method: 'POST',
        data: param || {}
    });
}

/* 添加角色 */
export function addRole(role){
    return service({
        url: '/Role/add',
        method: 'POST',
        data: role
    });
}

/* 更新角色 */
export function updateRole(role){
    return service({
        url: '/Role/update',
        method: 'PUT',
        data: role
    });
}

/* 删除角色 */
export function deleteRole(id){
    return service({
        url: `/Role/delete/${id}`,
        method: 'DELETE'
    });
}

/* 角色详情（含已分配权限ID列表） */
export function getRole(id){
    return service({
        url: `/Role/getRole/${id}`,
        method: 'GET'
    });
}

/* 全部权限点列表 */
export function permissionList(){
    return service({
        url: '/Role/permissions',
        method: 'GET'
    });
}

/* 给角色分配权限 */
export function assignPermissions(roleId, permissionIds){
    return service({
        url: '/Role/assignPermissions',
        method: 'POST',
        data: { roleId, permissionIds }
    });
}
