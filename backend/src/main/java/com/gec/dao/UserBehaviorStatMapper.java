package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.analysis.UserBehaviorStat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserBehaviorStatMapper extends BaseMapper<UserBehaviorStat> {

    /* 按 dimension_type 查询，支持日期范围过滤 */
    List<UserBehaviorStat> selectByDimensionType(
            @Param("dimensionType") String dimensionType,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 按多个 dimension_type 查询（用于合并看板） */
    List<UserBehaviorStat> selectByDimensionTypes(
            @Param("types") List<String> types,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 按 dimension_type 查询 TOP N，按指定字段排序 */
    List<UserBehaviorStat> selectTopByDimensionType(
            @Param("dimensionType") String dimensionType,
            @Param("orderField") String orderField,
            @Param("limit") Integer limit,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 热门商品榜（合并 product_view/product_cart/product_fav，按 pv 排序） */
    List<UserBehaviorStat> selectTopProducts(
            @Param("limit") Integer limit,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
