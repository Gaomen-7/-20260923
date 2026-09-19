package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.GoodsAttrGroup;

import java.util.List;

public interface IAttrGroupService
        extends IService<GoodsAttrGroup> {
    IPage listAttrGroup(Page page, Integer categoryId);

    void addAttrGroup(GoodsAttrGroup attrGroup);

    void updateAttrGroup(GoodsAttrGroup attrGroup);

    void deleteAttrGroup(Integer id);

    /** 查询某分类下的属性分组列表（下拉选项用） */
    List<GoodsAttrGroup> listGroupsByCategory(Integer categoryId);
}
