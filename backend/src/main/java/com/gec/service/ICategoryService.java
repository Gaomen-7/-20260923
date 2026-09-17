package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.Category;
import com.gec.domain.entity.Node;
import com.gec.domain.vo.BrandVO;
import com.gec.domain.vo.CategoryBrandVO;

import java.util.List;
import java.util.Map;

public interface ICategoryService
        extends IService<Category> {

    List<Node> listCategory();
    Integer[] getPidsArr(Integer id);
    String getPids(Integer id);

    void associateBrand(CategoryBrandVO cbVO);

    IPage<BrandVO> getListByBrand(
        Page page, Map data );
}
