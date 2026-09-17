package com.gec.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SkuGenerateVO
    implements Serializable {

    // ===== 输入：销售属性选中值列表 =====
    private List<SkuAttrItem> attrItems;

    // ===== 输出：笛卡尔积生成的SKU行列表 =====
    private List<SkuRow> skuRows;

    /*
    *   销售属性项：前端提交的每个销售属性的选中值。
    */
    @Data
    public static class SkuAttrItem
        implements Serializable {
        private Integer attrId;
        private String attrName;
        private String attrValue;  //选中值，多值用分号分隔，如 "黑色;白色"
    }

    /*
    *   SKU行：笛卡尔积的每一种组合。
    */
    @Data
    public static class SkuRow
        implements Serializable {
        private Map<String, String> attrValues;  //key=属性名, value=属性值（动态列）
        private String skuName;    //默认自动拼接，如 "黑色 128GB 标准版"
        private String skuTitle;   //可编辑，默认空
        private String skuSubtitle; //可编辑，默认空
        private BigDecimal price;   //可编辑，默认空
        private Boolean enabled;    //默认true，可取消
        private List<Map<String,Object>> discounts;  //折扣列表，默认空
        private List<Map<String,Object>> reductions;  //满减列表，默认空
        private String[] albumImages;  //选中的图集图片文件名，默认空
        private String defaultImage;   //默认图片文件名，默认空
    }
}
