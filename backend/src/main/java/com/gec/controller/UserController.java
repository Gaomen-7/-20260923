package com.gec.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.domain.entity.User;
import com.gec.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/User")
public class UserController extends BaseController {
    /* 1.自动装配 UserService 接口. */
    @Autowired
    private IUserService userService;

    /* 登录接口 */
    @PostMapping(
            value="/login",
            produces="application/json;charset=UTF-8"
    )
    public R login(@RequestBody Map map){
        try{
            String account = (String) map.get("account");
            String password = (String) map.get("password");
            User user = userService.login(account, password);
            // 生成简单 token（UUID，课程作业级别，不做 JWT）
            String token = java.util.UUID.randomUUID().toString().replace("-", "");
            // 密码不返回给前端
            user.setPassword(null);
            return R.ok().put("token", token).put("user", user);
        } catch(Exception e){
            e.printStackTrace();
            return R.err(e);
        }
    }

	/* 2.用户列表 list(). */
    @PostMapping(
            value="/list/{page}/{limit}",
            produces="application/json;charset=UTF-8"
    )
    public R list(
            @PathVariable("page")Integer page,
            @PathVariable("limit")Integer limit,
            @RequestBody Map map){
            /*把页码和页大小封装成一个对象*/
            Page frmPage = newPage(page, limit);
            IPage retPage = null;
            try{
                /*用户service获取用户列表*/
                retPage=userService.listUser(frmPage,map);
                /*转retPage转为R。MVC 会自动转为JSON格式*/
                return R.convertPage(retPage);
            }catch(Exception e){
                e.printStackTrace();
                return R.err(e);
            }
    }

    /* 3.添加用户-POST请求. */
    @PostMapping(
            value="/addUser/{roleId}",
            produces = "application/json;charset=UTF-8"
    )
    public R addUser(
            @PathVariable("roleId")Integer roleId,
            @RequestBody User user
    ){
        try{
            /*调用service实现用户添加*/
            userService.saveUser(user, roleId);
            /*操作成功，返回一个表示成功的JSON数据*/
            return R.ok();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return R.err(e);
        }
    }

    /* 4.更新用户-PUT请求. */
    @PutMapping(
            value="/updateUser/{roleId}",
            produces = "application/json;charset=UTF-8"
    )
    public R updateUser(
            @PathVariable("roleId")Integer roleId,
            @RequestBody User user
    ){
        try{
            /*调用service实现用户添加*/
            userService.saveUser(user, roleId);
            /*操作成功，返回一个表示成功的JSON数据*/
            return R.ok();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return R.err(e);
        }
    }

    /* 5.获取用户-GET请求.*/
    @GetMapping(
            value="/getUser/{id}",
            produces = "application/json;charset=UTF-8"
    )
    public R getUser(
            @PathVariable("id")Integer id){
        try{
            /*调用service实现用户添加*/
            User user = userService.getUser(id);
            /*操作成功，返回一个表示成功的JSON数据*/
            return R.ok(user);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return R.err(e);
        }
    }

    /* 6.删除用户-DELETE请求. */
    @DeleteMapping(
            value="/deleteUser/{id}",
            produces = "application/json;charset=UTF-8"
    )
    public R deleteUser(@PathVariable("id") Integer id){
        try{
            userService.deleteUser(id);
            return R.ok();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return R.err(e);
        }
    }

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }
}
