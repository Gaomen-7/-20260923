package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.Advert;
import com.gec.domain.search.AdvertSearch;

import java.util.List;

public interface IAdvertService extends IService<Advert> {

    /* 分页查询广告列表 */
    IPage<Advert> pageAdvert(Page page, AdvertSearch search);

    /* 新增广告 */
    void addAdvert(Advert advert);

    /* 更新广告 */
    void updateAdvert(Advert advert);

    /* 批量删除 */
    void batchDelete(List<Integer> ids);

    /* 上下线切换 */
    void toggleStatus(Integer id, Integer status);

    /* 刷新过期广告状态：将已过期且仍在投放中的广告批量下线 */
    int refreshExpired();

    /* 曝光量+1 */
    void incrementView(Integer id);

    /* 点击量+1 */
    void incrementClick(Integer id);
}
