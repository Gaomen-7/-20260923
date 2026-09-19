package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.entity.Dept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DeptMapper extends BaseMapper<Dept> {

    //{1}获取某部门的用户数量.
    @Select("SELECT count(*) cnt FROM tbl_user u WHERE u.dept_id=#{deptId}")
    int getUserCount(@Param("deptId") Integer deptId);

    //{2}获取子部门的数量 (某个部门下有几个 "一级" 下属部门)
    @Select("SELECT count(*) FROM tbl_dept d WHERE d.parent_id=#{deptId}")
    int getSubDeptCount(@Param("deptId") Integer deptId);

    //List<Dept> getList();
}







