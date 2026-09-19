package com.gec.service.impl;

import com.gec.dao.ChatAnalysisStatMapper;
import com.gec.dao.OrderAnalysisStatMapper;
import com.gec.dao.ReviewAnalysisStatMapper;
import com.gec.dao.UserBehaviorStatMapper;
import com.gec.domain.analysis.ChatAnalysisStat;
import com.gec.domain.analysis.OrderAnalysisStat;
import com.gec.domain.analysis.ReviewAnalysisStat;
import com.gec.domain.analysis.UserBehaviorStat;
import com.gec.service.IAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据分析服务实现（只读，透传各 StatMapper 的宽表查询）
 */
@Service
public class AnalysisServiceImpl implements IAnalysisService {
    @Autowired
    private UserBehaviorStatMapper userBehaviorStatMapper;
    @Autowired
    private OrderAnalysisStatMapper orderAnalysisStatMapper;
    @Autowired
    private ChatAnalysisStatMapper chatAnalysisStatMapper;
    @Autowired
    private ReviewAnalysisStatMapper reviewAnalysisStatMapper;

    /* ===== 用户行为分析 ===== */

    @Override
    public List<UserBehaviorStat> behaviorByDimension(
            String dimensionType, String startDate, String endDate) {
        return userBehaviorStatMapper.selectByDimensionType(dimensionType, startDate, endDate);
    }

    @Override
    public List<UserBehaviorStat> behaviorTopByDimension(
            String dimensionType, String metric, Integer limit, String startDate, String endDate) {
        return userBehaviorStatMapper.selectTopByDimensionType(dimensionType, metric, limit, startDate, endDate);
    }

    @Override
    public List<UserBehaviorStat> behaviorTopProducts(
            Integer limit, String startDate, String endDate) {
        return userBehaviorStatMapper.selectTopProducts(limit, startDate, endDate);
    }

    /* ===== 订单分析 ===== */

    @Override
    public List<OrderAnalysisStat> orderByDimension(
            String dimensionType, String startDate, String endDate) {
        return orderAnalysisStatMapper.selectByDimensionType(dimensionType, startDate, endDate);
    }

    @Override
    public List<OrderAnalysisStat> orderByDimensions(
            List<String> dimensionTypes, String startDate, String endDate) {
        return orderAnalysisStatMapper.selectByDimensionTypes(dimensionTypes, startDate, endDate);
    }

    @Override
    public List<OrderAnalysisStat> orderHourlySales(String startDate, String endDate) {
        return orderAnalysisStatMapper.selectHourlySales(startDate, endDate);
    }

    /* ===== 交流分析 ===== */

    @Override
    public List<ChatAnalysisStat> chatByDimension(
            String dimensionType, String startDate, String endDate) {
        return chatAnalysisStatMapper.selectByDimensionType(dimensionType, startDate, endDate);
    }

    @Override
    public List<ChatAnalysisStat> chatTopConsultedProducts(
            Integer limit, String startDate, String endDate) {
        return chatAnalysisStatMapper.selectTopConsultedProducts(limit, startDate, endDate);
    }

    /* ===== 评价分析 ===== */

    @Override
    public List<ReviewAnalysisStat> reviewByDimension(
            String dimensionType, String startDate, String endDate) {
        return reviewAnalysisStatMapper.selectByDimensionType(dimensionType, startDate, endDate);
    }

    @Override
    public List<ReviewAnalysisStat> reviewByDimensions(
            List<String> dimensionTypes, String startDate, String endDate) {
        return reviewAnalysisStatMapper.selectByDimensionTypes(dimensionTypes, startDate, endDate);
    }

    @Override
    public List<ReviewAnalysisStat> reviewProductWordOfMouth(
            Integer limit, String startDate, String endDate) {
        return reviewAnalysisStatMapper.selectProductWordOfMouth(limit, startDate, endDate);
    }
}
