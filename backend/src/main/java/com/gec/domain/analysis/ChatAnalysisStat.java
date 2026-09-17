package com.gec.domain.analysis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

/**
 * 交流分析结果表实体（对应 MySQL ads_chat_analysis）
 */
@TableName("ads_chat_analysis")
public class ChatAnalysisStat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String statDate;
    private String dimensionType;
    private String dimensionValue;
    private Integer sessionCount;
    private Integer userCount;
    private BigDecimal avgDuration;
    private BigDecimal avgFirstResponse;
    private BigDecimal avgReplyCount;
    private BigDecimal resolutionRate;
    private Integer conversionCount;
    private BigDecimal conversionRate;
    private BigDecimal invalidRate;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getStatDate() { return statDate; }
    public void setStatDate(String statDate) { this.statDate = statDate; }
    public String getDimensionType() { return dimensionType; }
    public void setDimensionType(String dimensionType) { this.dimensionType = dimensionType; }
    public String getDimensionValue() { return dimensionValue; }
    public void setDimensionValue(String dimensionValue) { this.dimensionValue = dimensionValue; }
    public Integer getSessionCount() { return sessionCount; }
    public void setSessionCount(Integer sessionCount) { this.sessionCount = sessionCount; }
    public Integer getUserCount() { return userCount; }
    public void setUserCount(Integer userCount) { this.userCount = userCount; }
    public BigDecimal getAvgDuration() { return avgDuration; }
    public void setAvgDuration(BigDecimal avgDuration) { this.avgDuration = avgDuration; }
    public BigDecimal getAvgFirstResponse() { return avgFirstResponse; }
    public void setAvgFirstResponse(BigDecimal avgFirstResponse) { this.avgFirstResponse = avgFirstResponse; }
    public BigDecimal getAvgReplyCount() { return avgReplyCount; }
    public void setAvgReplyCount(BigDecimal avgReplyCount) { this.avgReplyCount = avgReplyCount; }
    public BigDecimal getResolutionRate() { return resolutionRate; }
    public void setResolutionRate(BigDecimal resolutionRate) { this.resolutionRate = resolutionRate; }
    public Integer getConversionCount() { return conversionCount; }
    public void setConversionCount(Integer conversionCount) { this.conversionCount = conversionCount; }
    public BigDecimal getConversionRate() { return conversionRate; }
    public void setConversionRate(BigDecimal conversionRate) { this.conversionRate = conversionRate; }
    public BigDecimal getInvalidRate() { return invalidRate; }
    public void setInvalidRate(BigDecimal invalidRate) { this.invalidRate = invalidRate; }
}
