package com.gec.domain.vo;

import com.gec.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsAttrVO extends BaseEntity {
    private Integer id;           //id
    private String attrName;      //brand_name
    private Integer attrType;     //1:基本属性 2:销售属性

    private Integer valueType;    //1:单值 | 2:多值
    private String attrValue;     //brand_desc

    private Integer enable;         //
    private Integer categoryId;     //
    private Integer attrGroupId;    //
    private Integer searchEnable;   //show_status

}
