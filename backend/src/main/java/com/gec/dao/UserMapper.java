package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.domain.bo.UserBO;
import com.gec.domain.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserMapper extends BaseMapper<User> {

    Page<UserBO> getList(
       Page page,
       @Param("param") Map<String, Object> param );

}
