package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.gec.domain.vo.OptionVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OptionMapper
    extends BaseMapper<OptionVO> {

    @Select("SELECT id value, group_name label "+
            " FROM tbl_goods_attr_group "+
            " WHERE category_id=#{categoryId}" )
    List<OptionVO> groupByCategory(
        @Param("categoryId") Integer categoryId);

    @Select("SELECT id value, role_name label "+
            " FROM tbl_role "+
            " WHERE id not in(1)")
    List<OptionVO> roleOptions();

    @Select(" SELECT bc.brand_id value, bc.brand_name label "+
            " FROM tbl_brand_category bc "+
            " WHERE bc.category_id=#{categoryId} ")
    List<OptionVO> brandOptions(
        @Param("categoryId") Integer categoryId);

}
