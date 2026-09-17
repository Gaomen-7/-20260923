package com.gec.domain.analysis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

/**
 * 评价分析结果表实体（对应 MySQL ads_review_analysis）
 */
@TableName("ads_review_analysis")
public class ReviewAnalysisStat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String statDate;
    private String dimensionType;
    private String dimensionValue;
    private Integer totalReviews;
    private Integer goodReviews;
    private Integer midReviews;
    private Integer badReviews;
    private BigDecimal goodRate;
    private BigDecimal avgRating;
    private Integer keywordCount;
    private Integer appendCount;
    private BigDecimal appendRate;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getStatDate() { return statDate; }
    public void setStatDate(String statDate) { this.statDate = statDate; }
    public String getDimensionType() { return dimensionType; }
    public void setDimensionType(String dimensionType) { this.dimensionType = dimensionType; }
    public String getDimensionValue() { return dimensionValue; }
    public void setDimensionValue(String dimensionValue) { this.dimensionValue = dimensionValue; }
    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
    public Integer getGoodReviews() { return goodReviews; }
    public void setGoodReviews(Integer goodReviews) { this.goodReviews = goodReviews; }
    public Integer getMidReviews() { return midReviews; }
    public void setMidReviews(Integer midReviews) { this.midReviews = midReviews; }
    public Integer getBadReviews() { return badReviews; }
    public void setBadReviews(Integer badReviews) { this.badReviews = badReviews; }
    public BigDecimal getGoodRate() { return goodRate; }
    public void setGoodRate(BigDecimal goodRate) { this.goodRate = goodRate; }
    public BigDecimal getAvgRating() { return avgRating; }
    public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
    public Integer getKeywordCount() { return keywordCount; }
    public void setKeywordCount(Integer keywordCount) { this.keywordCount = keywordCount; }
    public Integer getAppendCount() { return appendCount; }
    public void setAppendCount(Integer appendCount) { this.appendCount = appendCount; }
    public BigDecimal getAppendRate() { return appendRate; }
    public void setAppendRate(BigDecimal appendRate) { this.appendRate = appendRate; }
}
