package com.gec.domain.bo;

import com.gec.domain.entity.BaseEntity;
import com.gec.domain.entity.GoodsAttrValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsAttrBO extends BaseEntity {
    private Integer id;           //id
    private String attrName;      //brand_name

    private Integer attrType;      //1:基本属性 2:销售属性
    private String  attrTypeName;  //[BO Attr]

    private Integer valueType;      //1:单值 | 2:多值
    private String  valueTypeName;  //单值 | 多值

    private String  attrValue;     /* 属性值.   */
    private Integer enable;        /* 激活使用. */
    private Integer categoryId;    /* 类别 ID   */

    private Integer searchEnable;   //

    private Integer attrGroupId; //RA.attr_group_id
    private String  groupName;   //g.group_name

    public GoodsAttrBO() {}

    public String getAttrTypeName(){
        if( attrType==null ) return null;
        String typeName = (this.attrType==1) ? "基本属性" : "销售属性";
        return typeName;
    }

    public String getValueTypeName(){
        if( valueType==null ) return null;
        String typeName = (this.valueType==1) ? "单值型" : "多值型";
        return typeName;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public void setAttrValue(GoodsAttrValue val) {
        this.valueType = val.getValueType();
        this.valueTypeName = (valueType==1) ? "单值" : "多值";
        this.attrValue = val.getAttrValue();
        System.out.printf("type:%s, value:%s\n",
                valueTypeName, attrValue);
    }

}

/*
    //有了映射器, 以下构造器可以废除..
//    public GoodsAttrBO(GoodsAttr attr) {
//        this.id = attr.getId();
//        this.attrType = attr.getAttrType();
//        this.attrName = attr.getAttrName();
//        this.attrTypeName = (attrType==1) ? "基本属性" : "销售属性";
//        this.enable = attr.getEnable();
//        this.searchEnable = attr.getSearchEnable();
//        this.categoryId = attr.getCategoryId();
//    }

    select
        att.*, val.attr_value,
        if(att.attr_type=1, '基本属性','销售属性') attr_type_name,
        if(val.value_type=1, '单值','多值') value_type_name
    from tbl_goods_attr att
    left join tbl_goods_attr_value val
    on att.id = val.attr_id
    where att.category_id=11;

    表头格式:
    +----+-----------+-------------+-----------+---------------+--------+----------------+----------------+-----------------+
    | id | attr_name | category_id | attr_type | search_enable | enable | attr_value     | attr_type_name | value_type_name |
    +----+-----------+-------------+-----------+---------------+--------+----------------+----------------+-----------------+
* */