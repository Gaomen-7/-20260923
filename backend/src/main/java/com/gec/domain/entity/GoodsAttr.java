package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName(value="tbl_goods_attr")
@EqualsAndHashCode(callSuper = false)
public class GoodsAttr extends BaseEntity {
    @TableId(type= IdType.AUTO)
    private Integer id;            //id
    private String  attrName;      //attr_name
    private Integer categoryId;    //category_id
    private Integer attrType;      //attr_type

    private Integer valueType;    //1:单值 | 2:多值
    private String  attrValue;    //brand_desc

    private Integer enable;        //enable
    private Integer searchEnable;  //search_enable

    //attrGroupId

    //create_date [In BaseEntity]
    //update_date [In BaseEntity]
}
