package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.analysis.OrderAnalysisStat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderAnalysisStatMapper extends BaseMapper<OrderAnalysisStat> {

    /* 按 dimension_type 查询，支持日期范围过滤 */
    List<OrderAnalysisStat> selectByDimensionType(
            @Param("dimensionType") String dimensionType,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 按多个 dimension_type 查询（用于退换货看板 return_reason+cancel） */
    List<OrderAnalysisStat> selectByDimensionTypes(
            @Param("types") List<String> types,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 按 dimension_type 查询 TOP N，按指定字段排序 */
    List<OrderAnalysisStat> selectTopByDimensionType(
            @Param("dimensionType") String dimensionType,
            @Param("orderField") String orderField,
            @Param("limit") Integer limit,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /* 时段销售汇总（按 dimension_value 分组，求和订单数与金额） */
    List<OrderAnalysisStat> selectHourlySales(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
