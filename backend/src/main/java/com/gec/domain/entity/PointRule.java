package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.node.DecimalNode;
import lombok.Data;

@Data
@TableName(value="tbl_point_rule")
public class PointRule implements java.io.Serializable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private Integer goodsId;
    private Integer rewardPoint;
    private Integer allowDeduct;
    private Double deductMaxRate;
    private String status;
    private String createDate;
    private String updateDate;

}
