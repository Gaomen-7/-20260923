package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.analysis.ChatAnalysisStat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatAnalysisStatMapper extends BaseMapper<ChatAnalysisStat> {

    /* 按 dimension_type 查询，支持日期范围过滤 */
    List<ChatAnalysisStat> selectByDimensionType(
            @Param("dimensionType") String dimensionType,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 按多个 dimension_type 查询（用于合并看板） */
    List<ChatAnalysisStat> selectByDimensionTypes(
            @Param("types") List<String> types,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 按 dimension_type 查询 TOP N，按指定字段排序 */
    List<ChatAnalysisStat> selectTopByDimensionType(
            @Param("dimensionType") String dimensionType,
            @Param("orderField") String orderField,
            @Param("limit") Integer limit,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 热门咨询商品榜（product 维度，按会话数排序） */
    List<ChatAnalysisStat> selectTopConsultedProducts(
            @Param("limit") Integer limit,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
