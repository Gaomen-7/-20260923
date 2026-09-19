package com.gec.service;

import com.gec.domain.analysis.ChatAnalysisStat;
import com.gec.domain.analysis.OrderAnalysisStat;
import com.gec.domain.analysis.ReviewAnalysisStat;
import com.gec.domain.analysis.UserBehaviorStat;

import java.util.List;

/**
 * 数据分析服务（聚合行为/订单/交流/评价四个分析域，
 * 数据源为 ads_* 宽表，只读查询）
 */
public interface IAnalysisService {

    /* ===== 用户行为分析（ads_user_behavior） ===== */

    List<UserBehaviorStat> behaviorByDimension(String dimensionType, String startDate, String endDate);

    List<UserBehaviorStat> behaviorTopByDimension(String dimensionType, String metric, Integer limit, String startDate, String endDate);

    List<UserBehaviorStat> behaviorTopProducts(Integer limit, String startDate, String endDate);

    /* ===== 订单分析（ads_order_analysis） ===== */

    List<OrderAnalysisStat> orderByDimension(String dimensionType, String startDate, String endDate);

    List<OrderAnalysisStat> orderByDimensions(List<String> dimensionTypes, String startDate, String endDate);

    List<OrderAnalysisStat> orderHourlySales(String startDate, String endDate);

    /* ===== 交流分析（ads_chat_analysis） ===== */

    List<ChatAnalysisStat> chatByDimension(String dimensionType, String startDate, String endDate);

    List<ChatAnalysisStat> chatTopConsultedProducts(Integer limit, String startDate, String endDate);

    /* ===== 评价分析（ads_review_analysis） ===== */

    List<ReviewAnalysisStat> reviewByDimension(String dimensionType, String startDate, String endDate);

    List<ReviewAnalysisStat> reviewByDimensions(List<String> dimensionTypes, String startDate, String endDate);

    List<ReviewAnalysisStat> reviewProductWordOfMouth(Integer limit, String startDate, String endDate);
}
