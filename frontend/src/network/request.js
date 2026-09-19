import axios from 'axios'
import { Message } from 'element-ui'
import { API_BASE } from '@/utils/imageUrl'

/* 1.创建 axios(网络通信组件) 实例对象。 */
const service = axios.create({
    baseURL: API_BASE,
    timeout: 0
});

/* 2.添加请求拦截器。 */
service.interceptors.request.use(
    req=>{
        /* 以后,登录之后会把 token 保存到浏览器本地存储。
        1.这里获取本地存储的 token。
        设置到请求头,一并携带到WEB接口。
        (服务器可以获得token,并验证你是否登录)
        */
        let token = window.sessionStorage.getItem('token');
        if( token ){
            req.headers['token'] = token;
        }
        return req;
    },
    error=>{
        return Promise.reject(error);
    }
);

/* 3.添加响应拦截器。 */
service.interceptors.response.use(
    resp=>{
        /* 1.把响应对象中的数据实体,返回给上层调用者。 */
        return resp.data;
    },
    error=>{
        /* 1.当有错误发生时,将会进去此区域。 */
        let resp = error.response;
        /* 2.当网络不通,服务器连接不上, resp==undefined */
        if( !resp ){
            Message.error({message: '服务器无法连接(请确认网络正常).'});
            return Promise.reject( error );
        }
        /* 3.服务器有连接有响应(但有错误) */
        let status = resp.status;  /* 获取它的状态码。 */
        let data = resp.data;
        if( status==404 ){
            Message.error({message: '找不到相关的资源!'});
        }else if( status==403 ){
            Message.error({message: '你没有权限访问该资源!'});
        }else if( status==401 ){
            Message.error({message: '你没有登录该系统!'});
        }else{
            Message.error({message: `发生内部错误, 原因: ${data.cause}`});
        }
        return Promise.reject( error );
    }
);

/* 4.导出实例对象。 */
export default service;
