package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.GoodsInfo;
import com.gec.domain.entity.SkuInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品管理 Service 接口
 * 将 GoodsController 中直接操作 Mapper 的逻辑下沉到 Service 层
 */
public interface IGoodsService extends IService<GoodsInfo> {

    /**
     * 分页查询 SPU 商品列表，支持分类/品牌/状态/关键词/推荐/价格区间/编号/库存预警筛选，按 id 倒序
     */
    IPage<GoodsInfo> pageGoods(Integer page, Integer limit, Integer categoryId, Integer brandId, Integer publishStatus, Integer isRecommend, String keyword, BigDecimal minPrice, BigDecimal maxPrice, String goodsSn, Integer lowStock);

    /**
     * 分页查询 SKU 列表，支持分类/品牌/价格区间/关键词筛选，按 sku_id 倒序
     */
    IPage<SkuInfo> pageSku(Integer page, Integer limit, Integer categoryId, Integer brandId, BigDecimal minPrice, BigDecimal maxPrice, String keyword);

    /**
     * 更新商品上架/下架状态
     */
    void updateStatus(Integer id, Integer publishStatus);

    /**
     * 批量更新上架/下架状态
     */
    void batchUpdateStatus(List<Integer> ids, Integer publishStatus);

    /**
     * 批量删除商品
     */
    void batchDelete(List<Integer> ids);

    /**
     * 切换推荐状态
     */
    void toggleRecommend(Integer id, Integer isRecommend);

    /**
     * 批量推荐/取消推荐
     */
    void batchRecommend(List<Integer> ids, Integer isRecommend);

    /**
     * 更新商品基本信息，内含上架锁定校验（ADR-001：上架商品不可修改，需先下架）
     */
    void updateGoods(GoodsInfo goods);
}
