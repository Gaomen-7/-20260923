package com.gec.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.UserMapper;
import com.gec.domain.bo.UserBO;
import com.gec.domain.entity.User;
import com.gec.service.IRoleService;
import com.gec.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class UserServiceImpl
    extends ServiceImpl<UserMapper,User>
    implements IUserService {
    /*
    *  以下提示注入错误, 可以这样处理。
    *  ALT + ENTER
    */
    @Autowired
    private UserMapper userMapper;
    @Autowired                        //(打开注释)
    private IRoleService roleService; //(打开注释)

    @Override
    public IPage<UserBO> listUser(
        Page page, Map<String, Object> param ) {
        Page<UserBO> retPage = userMapper.getList(page, param);
		/*-- 请填入代码 --*/
        return retPage;
    }

    @Override
    public void saveUser(User user, Integer roleId) {
        boolean ret = saveOrUpdate(user);
        if(!ret){
            throw new RuntimeException("保存用户失败。");
        }
        Integer userId = user.getId();
        /*返回带有分页信息的对象出去*/
        roleService.addUserRoleAssociation(userId, roleId);
    }

    @Override
    public User getUser(Integer id) {

        return getById(id);
    }
    @Override
    public void deleteUser(Integer id) {

    }

    @Override
    public User login(String account, String password) {
        // 1. 按账号查询用户
        User user = lambdaQuery().eq(User::getAccount, account).one();
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        // 2. 密码为空的用户不允许登录
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("该账号未设置密码，请联系管理员");
        }
        // 3. 密码比对（项目无 spring-security-crypto 依赖，明文比对）
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("账号或密码错误");
        }
        return user;
    }
}
