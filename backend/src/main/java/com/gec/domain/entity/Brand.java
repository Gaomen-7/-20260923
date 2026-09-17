package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gec.domain.entity.BaseEntity;
import com.gec.domain.entity.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName(value="tbl_brand")
@EqualsAndHashCode(callSuper = false)
public class Brand extends BaseEntity {
    @TableId(type= IdType.AUTO)
    private Integer id;          //id
    private String brandName;    //brand_name
    @TableField(value="logo_name")
    private String logoUrl;     //logo_url（表字段为 logo_name）
    private String brandDesc;    //brand_desc
    private Integer showStatus;  //show_status

    //create_date
    //update_date
}
