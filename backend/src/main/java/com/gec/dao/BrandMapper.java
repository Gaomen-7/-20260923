package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.domain.entity.Brand;
import com.gec.domain.search.BrandSearch;
import com.gec.domain.vo.BrandCategoryVO;
import com.gec.domain.vo.BrandVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BrandMapper
     extends BaseMapper<Brand> {

    void removeAssociate(
         @Param("brandId") Integer brandId,
         @Param("categoryIds") List<Integer> categoryIds );

    int associateCategory(
         @Param("bcVO") BrandCategoryVO bcVO);

    Page<BrandVO> listByCategory(
            @Param("page") Page<BrandVO> page,
            @Param("param") BrandSearch param );

}
