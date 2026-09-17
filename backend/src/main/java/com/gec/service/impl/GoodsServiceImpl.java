package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.GoodsInfoMapper;
import com.gec.dao.SkuAlbumMapper;
import com.gec.dao.SkuInfoMapper;
import com.gec.domain.entity.GoodsInfo;
import com.gec.domain.entity.SkuAlbum;
import com.gec.domain.entity.SkuInfo;
import com.gec.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品管理 Service 实现
 * 将 GoodsController 中直接操作 Mapper 的逻辑和上架锁定校验下沉到 Service 层
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsInfoMapper, GoodsInfo> implements IGoodsService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuAlbumMapper skuAlbumMapper;

    @Override
    public IPage<GoodsInfo> pageGoods(Integer page, Integer limit, Integer categoryId, Integer brandId, Integer publishStatus, Integer isRecommend, String keyword, BigDecimal minPrice, BigDecimal maxPrice, String goodsSn, Integer lowStock) {
        QueryWrapper<GoodsInfo> qw = new QueryWrapper<>();
        if (categoryId != null) {
            qw.eq("category_id", categoryId);
        }
        if (brandId != null) {
            qw.eq("brand_id", brandId);
        }
        if (publishStatus != null) {
            qw.eq("publish_status", publishStatus);
        }
        if (isRecommend != null) {
            qw.eq("is_recommend", isRecommend);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            qw.like("goods_name", keyword.trim());
        }
        if (minPrice != null) {
            qw.ge("price", minPrice);
        }
        if (maxPrice != null) {
            qw.le("price", maxPrice);
        }
        if (goodsSn != null && !goodsSn.trim().isEmpty()) {
            qw.like("goods_sn", goodsSn.trim());
        }
        if (lowStock != null && lowStock == 1) {
            qw.lt("stock", 20);
        }
        qw.orderByDesc("id");
        return baseMapper.selectPage(new Page<>(page, limit), qw);
    }

    @Override
    public IPage<SkuInfo> pageSku(Integer page, Integer limit, Integer categoryId, Integer brandId, BigDecimal minPrice, BigDecimal maxPrice, String keyword) {
        QueryWrapper<SkuInfo> qw = new QueryWrapper<>();
        if (categoryId != null) {
            qw.eq("category_id", categoryId);
        }
        if (brandId != null) {
            qw.eq("brand_id", brandId);
        }
        if (minPrice != null) {
            qw.ge("price", minPrice);
        }
        if (maxPrice != null) {
            qw.le("price", maxPrice);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            qw.like("sku_name", keyword.trim());
        }
        qw.orderByDesc("sku_id");
        IPage<SkuInfo> pageResult = skuInfoMapper.selectPage(new Page<>(page, limit), qw);
        /* 批量填充默认图片 */
        fillDefaultImage(pageResult.getRecords());
        return pageResult;
    }

    /* 批量查询 SKU 图集，填充 defaultImage */
    private void fillDefaultImage(List<SkuInfo> skuList) {
        if (skuList == null || skuList.isEmpty()) {
            return;
        }
        List<Integer> skuIds = new java.util.ArrayList<>();
        for (SkuInfo sku : skuList) {
            skuIds.add(sku.getSkuId());
        }
        QueryWrapper<SkuAlbum> qw = new QueryWrapper<>();
        qw.in("sku_id", skuIds);
        List<SkuAlbum> albums = skuAlbumMapper.selectList(qw);
        Map<Integer, String> imgMap = new java.util.HashMap<>();
        for (SkuAlbum album : albums) {
            imgMap.put(album.getSkuId(), album.getDefaultImage());
        }
        for (SkuInfo sku : skuList) {
            sku.setDefaultImage(imgMap.get(sku.getSkuId()));
        }
    }

    @Override
    public void updateStatus(Integer id, Integer publishStatus) {
        GoodsInfo goods = new GoodsInfo();
        goods.setId(id);
        goods.setPublishStatus(publishStatus);
        baseMapper.updateById(goods);
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Integer> ids, Integer publishStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择商品");
        }
        UpdateWrapper<GoodsInfo> uw = new UpdateWrapper<>();
        uw.in("id", ids).set("publish_status", publishStatus);
        boolean ret = this.update(uw);
        if (!ret) {
            throw new RuntimeException("批量操作失败");
        }
    }

    @Override
    @Transactional
    public void batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择商品");
        }
        boolean ret = this.removeByIds(ids);
        if (!ret) {
            throw new RuntimeException("批量删除失败");
        }
    }

    @Override
    public void toggleRecommend(Integer id, Integer isRecommend) {
        GoodsInfo goods = new GoodsInfo();
        goods.setId(id);
        goods.setIsRecommend(isRecommend);
        baseMapper.updateById(goods);
    }

    @Override
    @Transactional
    public void batchRecommend(List<Integer> ids, Integer isRecommend) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择商品");
        }
        UpdateWrapper<GoodsInfo> uw = new UpdateWrapper<>();
        uw.in("id", ids).set("is_recommend", isRecommend);
        boolean ret = this.update(uw);
        if (!ret) {
            throw new RuntimeException("批量推荐操作失败");
        }
    }

    @Override
    public void updateGoods(GoodsInfo goods) {
        if (goods.getId() == null) {
            throw new RuntimeException("商品ID不能为空");
        }
        GoodsInfo exist = baseMapper.selectById(goods.getId());
        if (exist == null) {
            throw new RuntimeException("商品不存在");
        }
        // ADR-001：上架商品不可修改，需先下架
        if (exist.getPublishStatus() != null && exist.getPublishStatus() == 1) {
            throw new RuntimeException("上架商品不可修改，请先下架");
        }
        baseMapper.updateById(goods);
    }
}
