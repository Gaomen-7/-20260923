package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.Brand;
import com.gec.domain.search.BrandSearch;
import com.gec.domain.vo.BrandCategoryVO;
import com.gec.domain.vo.BrandVO;

import java.util.Map;

public interface IBrandService extends IService<Brand> {
    IPage<Brand> listBrand(
        Page page, BrandSearch param);
    void associateCategory(BrandCategoryVO bcVO);

    IPage<BrandVO> getListByCategory(
        Page page, BrandSearch param);

    void addBrand(Brand brand);

    void updateBrand(Brand brand);

    /** 根据类别ID查询关联的品牌选项（下拉用） */
    java.util.List<com.gec.domain.vo.OptionVO> brandOptions(Integer categoryId);
}


