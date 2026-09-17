package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value="tbl_spu_goods_attr")
public class SpuGoodsAttr implements java.io.Serializable {
    @TableId(type= IdType.AUTO)
    private Integer id;            //id
    private Integer goodsId;       //goods_id
    private Integer attrId;        //attr_id
    private String  attrName;      //attr_name
    private String  attrValue;     //attr_value
    private Integer attrSort;      //attr_sort
}
