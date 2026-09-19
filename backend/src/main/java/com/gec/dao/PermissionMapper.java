package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.entity.Permission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {

    /** 查询某角色已拥有的权限ID列表 */
    @Select("SELECT permission_id FROM tbl_role_permission WHERE role_id=#{roleId}")
    List<Integer> findPermissionIdsByRoleId(@Param("roleId") Integer roleId);

    /** 删除某角色的全部权限关联 */
    @Delete("DELETE FROM tbl_role_permission WHERE role_id=#{roleId}")
    int deleteByRoleId(@Param("roleId") Integer roleId);

    /** 批量插入角色-权限关联 */
    @Insert("<script>" +
            "INSERT INTO tbl_role_permission(role_id,permission_id) VALUES " +
            "<foreach collection='permissionIds' item='pid' separator=','>" +
            "(#{roleId},#{pid})" +
            "</foreach>" +
            "</script>")
    int batchInsertRolePermission(@Param("roleId") Integer roleId,
                                  @Param("permissionIds") List<Integer> permissionIds);
}
