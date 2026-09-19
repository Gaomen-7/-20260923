package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.domain.entity.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface RoleMapper extends BaseMapper<Role> {
    /*写入tbl_role数据，建立关联*/
    @Insert("INSERT INTO tbl_user_role(user_id,role_id) VALUES(#{userId},#{roleId})")
    int addUserRoleAssociation(
            @Param("userId")Integer userId,
            @Param("roleId")Integer roleId);


    /*移除用户与角色关联数据*/
    @Delete("DELETE FROM tbl_user_role WHERE user_id=#{userId}")
    int removeUserRoleAssociation(
            @Param("userId")Integer userId);

}
