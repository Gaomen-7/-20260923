import service from '@/network/request.js'

/* 获取部门树形列表。 */
export function getDeptList( id ){
    return service({
        url: '/Dept/list',
        method: 'GET'
    });
}

export function addDept( dept ){
    return service({
        url:`/Dept/addDept`,
        method:"POST",
        data: dept
    });
}

export function updateDept( dept ){
    return service({
        url:`/Dept/updateDept`,
        method:"PUT",
        data: dept
    });
}

export function deleteDept( deptId ){
    return service({
        url:`/Dept/deleteDept/${deptId}`,
        method:"DELETE"
    });
}
