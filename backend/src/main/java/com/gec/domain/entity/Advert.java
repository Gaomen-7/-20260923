package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "tbl_advert")
public class Advert implements java.io.Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String advertName;     // 广告名称
    private Integer advertType;    // 广告形式：1=图片，2=视频，3=GIF
    private String position;       // 投放位置
    private String imageUrl;       // 广告图片
    private String linkUrl;        // 跳转链接
    private Integer billingType;   // 计费方式：1=CPM，2=CPD
    private BigDecimal price;      // 单价
    private Integer totalViews;    // 售卖量
    private Integer currentViews;  // 已展现量
    private Integer clickCount;     // 点击量
    private String startTime;      // 投放开始时间
    private String endTime;        // 投放结束时间
    private Integer weight;        // 投放权重
    private Integer isFirst;       // 是否首刷：0=否，1=是
    private Integer status;        // 状态：0=已下线，1=投放中
    private String createTime;
    private String updateTime;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer effectiveStatus;  // 有效状态：0=已下线 1=投放中 2=未开始 3=已过期（非DB字段，查询时计算）
}
