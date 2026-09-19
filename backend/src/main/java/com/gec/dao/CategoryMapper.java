package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.entity.Category;
import com.gec.domain.vo.CategoryBrandVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper
        extends BaseMapper<Category> {

    /*-- 1.获取父 ID 序列(获取某个类别的上代序列) --*/
    @Select("SELECT p_id FROM tbl_goods_category WHERE id=#{id}")
    String getPids(Integer id);


    /* INSERT CODE. */


    int removeAssociate(
        @Param("categoryId")Integer categoryId,
        @Param("brandIds") List<Integer> brandIds
    );

    int associateBrand(@Param("cbVO") CategoryBrandVO cbVO);

    //List<Category> getList();
}


