package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.bo.GoodsAttrBO;
import com.gec.domain.entity.GoodsAttr;
import com.gec.domain.search.GoodsAttrSearch;
import com.gec.domain.vo.GoodsAttrVO;

import java.util.List;
import java.util.Map;

public interface IGoodsAttrService
        extends IService<GoodsAttr> {

    IPage<GoodsAttrBO> listGoodsAttr(
            Page page, GoodsAttrSearch attrSearch);
    void addGoodsAttr(GoodsAttrVO goodsAttr);
    void updateGoodsAttr(GoodsAttrVO goodsAttr);
    void deleteGoodsAttr(Integer id);

    /* 【发布商品专用方法】 */
    List<GoodsAttr> queryGoodsAttrByCategory(
         Integer categoryId, Integer attrType );

}
