import service from '@/network/request.js'

/* 获取角色下拉选项。 */
export function roleOptions(){
    return service({
        url: '/Role/roleOptions',
        method: 'GET'
    });
}
