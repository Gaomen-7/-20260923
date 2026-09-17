package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.GoodsAttrGroup;

public interface IAttrGroupService
        extends IService<GoodsAttrGroup> {
    IPage listAttrGroup(Page page, Integer categoryId);

    void addAttrGroup(GoodsAttrGroup attrGroup);

    void updateAttrGroup(GoodsAttrGroup attrGroup);

    void deleteAttrGroup(Integer id);
}
