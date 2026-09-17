package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.analysis.ReviewAnalysisStat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReviewAnalysisStatMapper extends BaseMapper<ReviewAnalysisStat> {

    /* 按 dimension_type 查询，支持日期范围过滤 */
    List<ReviewAnalysisStat> selectByDimensionType(
            @Param("dimensionType") String dimensionType,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 按多个 dimension_type 查询（用于词云 keyword_good+keyword_bad、口碑榜） */
    List<ReviewAnalysisStat> selectByDimensionTypes(
            @Param("types") List<String> types,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 按 dimension_type 查询 TOP N，按指定字段排序 */
    List<ReviewAnalysisStat> selectTopByDimensionType(
            @Param("dimensionType") String dimensionType,
            @Param("orderField") String orderField,
            @Param("limit") Integer limit,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 商品口碑榜（product_top/product_bottom 合并，按好评率排序） */
    List<ReviewAnalysisStat> selectProductWordOfMouth(
            @Param("limit") Integer limit,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
