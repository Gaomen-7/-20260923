package com.gec.domain.analysis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

/**
 * 订单分析结果表实体（对应 MySQL ads_order_analysis）
 */
@TableName("ads_order_analysis")
public class OrderAnalysisStat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String statDate;
    private String dimensionType;
    private String dimensionValue;
    private Integer orderCount;
    private Integer paidOrderCount;
    private BigDecimal totalAmount;
    private BigDecimal avgOrderValue;
    private BigDecimal payConversionRate;
    private Integer cancelCount;
    private Integer returnCount;
    private BigDecimal returnRate;
    private BigDecimal returnAmount;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getStatDate() { return statDate; }
    public void setStatDate(String statDate) { this.statDate = statDate; }
    public String getDimensionType() { return dimensionType; }
    public void setDimensionType(String dimensionType) { this.dimensionType = dimensionType; }
    public String getDimensionValue() { return dimensionValue; }
    public void setDimensionValue(String dimensionValue) { this.dimensionValue = dimensionValue; }
    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
    public Integer getPaidOrderCount() { return paidOrderCount; }
    public void setPaidOrderCount(Integer paidOrderCount) { this.paidOrderCount = paidOrderCount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getAvgOrderValue() { return avgOrderValue; }
    public void setAvgOrderValue(BigDecimal avgOrderValue) { this.avgOrderValue = avgOrderValue; }
    public BigDecimal getPayConversionRate() { return payConversionRate; }
    public void setPayConversionRate(BigDecimal payConversionRate) { this.payConversionRate = payConversionRate; }
    public Integer getCancelCount() { return cancelCount; }
    public void setCancelCount(Integer cancelCount) { this.cancelCount = cancelCount; }
    public Integer getReturnCount() { return returnCount; }
    public void setReturnCount(Integer returnCount) { this.returnCount = returnCount; }
    public BigDecimal getReturnRate() { return returnRate; }
    public void setReturnRate(BigDecimal returnRate) { this.returnRate = returnRate; }
    public BigDecimal getReturnAmount() { return returnAmount; }
    public void setReturnAmount(BigDecimal returnAmount) { this.returnAmount = returnAmount; }
}
