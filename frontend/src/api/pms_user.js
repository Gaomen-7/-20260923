import service from '@/network/request.js'

export function getUser( id ){
    return service({
        url: `/User/getUser/${id}`,
        method:"GET"
    });
}

/*
这是SpringBoot后台的方法格式:
@PostMapping(
    value="/list/{page}/{limit}"
)
R list(
    @PathVariable("page")Integer page,
    @PathVariable("limit")Integer limit,
    @RequestBody Map map );
*/
export function getUserList( page, limit, param ){
    return service({
        url: `/User/list/${page}/${limit}`,
        method:"POST",
        data:param
    });
}

/* 3.添加用户. */
export function addUser( user ){
    let roleId = user.roleId;
    return service({
        url: `/User/addUser/${roleId}`,
        method:"POST",
        data:user
    });
}

/* 4.更新用户. */
export function updateUser( user ){
    let roleId = user.roleId;
    return service({
        url: `/User/updateUser/${roleId}`,
        method:"PUT",
        data:user
    });
}

/* 5.删除用户. */
export function deleteUser( id ){
    return service({
        url: `/User/deleteUser/${id}`,
        method:"DELETE"
    });
}

/* 6.登录. */
export function login( account, password ){
    return service({
        url: `/User/login`,
        method:"POST",
        data:{ account, password }
    });
}
