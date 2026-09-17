package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName(value="tbl_goods_attr_value")
@EqualsAndHashCode(callSuper = false)
public class GoodsAttrValue {
    @TableId(type= IdType.AUTO)
    private Integer id;         //id
    private Integer spuId;      //spu_id
    private Integer attrId;     //attr_id
    private Integer valueType;  //value_type
    private String attrValue;   //attr_value


}
