package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.bo.UserBO;
import com.gec.domain.entity.User;

import java.util.Map;

public interface IUserService extends IService<User> {

    IPage<UserBO> listUser(
            Page page, Map<String, Object> data);

    void saveUser(User user, Integer roleId);

    User getUser(Integer id);

    void deleteUser(Integer id);

    User login(String account, String password);
}
