package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("tbl_member_level_rule")
public class MemberLevelRule {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String levelName;
    private Integer minPoints;
    private BigDecimal discountRate;
    private String remark;
    private String createTime;
    private String updateTime;
}
