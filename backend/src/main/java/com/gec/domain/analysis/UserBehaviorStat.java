package com.gec.domain.analysis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

/**
 * 用户行为分析结果表实体（对应 MySQL ads_user_behavior）
 */
@TableName("ads_user_behavior")
public class UserBehaviorStat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String statDate;
    private String dimensionType;
    private String dimensionValue;
    private Integer pv;
    private Integer uv;
    private Integer sessionCount;
    private Integer conversionCount;
    private BigDecimal conversionRate;
    private BigDecimal bounceRate;
    private BigDecimal avgDuration;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getStatDate() { return statDate; }
    public void setStatDate(String statDate) { this.statDate = statDate; }
    public String getDimensionType() { return dimensionType; }
    public void setDimensionType(String dimensionType) { this.dimensionType = dimensionType; }
    public String getDimensionValue() { return dimensionValue; }
    public void setDimensionValue(String dimensionValue) { this.dimensionValue = dimensionValue; }
    public Integer getPv() { return pv; }
    public void setPv(Integer pv) { this.pv = pv; }
    public Integer getUv() { return uv; }
    public void setUv(Integer uv) { this.uv = uv; }
    public Integer getSessionCount() { return sessionCount; }
    public void setSessionCount(Integer sessionCount) { this.sessionCount = sessionCount; }
    public Integer getConversionCount() { return conversionCount; }
    public void setConversionCount(Integer conversionCount) { this.conversionCount = conversionCount; }
    public BigDecimal getConversionRate() { return conversionRate; }
    public void setConversionRate(BigDecimal conversionRate) { this.conversionRate = conversionRate; }
    public BigDecimal getBounceRate() { return bounceRate; }
    public void setBounceRate(BigDecimal bounceRate) { this.bounceRate = bounceRate; }
    public BigDecimal getAvgDuration() { return avgDuration; }
    public void setAvgDuration(BigDecimal avgDuration) { this.avgDuration = avgDuration; }
}
