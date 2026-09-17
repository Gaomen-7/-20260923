package com.gec.service.publish;

import com.gec.domain.vo.SkuGenerateVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SKU生成器：根据销售属性项做笛卡尔积，生成SKU行。
 */
@Service
public class SkuGenerator {

    /**
     * 根据销售属性项生成SKU行（笛卡尔积）。
     *
     * @param attrItems 销售属性项，attrValue多值用分号分隔
     * @return SKU行列表；空输入返回空列表
     */
    public List<SkuGenerateVO.SkuRow> generate(List<SkuGenerateVO.SkuAttrItem> attrItems) {
        if (attrItems == null || attrItems.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 收集属性名与值数组
        List<String> attrNames = new ArrayList<>();
        List<String[]> valueArrays = new ArrayList<>();
        for (SkuGenerateVO.SkuAttrItem item : attrItems) {
            attrNames.add(item.getAttrName());
            valueArrays.add(item.getAttrValue().split(";"));
        }

        // 2. 逐个属性做笛卡尔积扩展
        List<Map<String, String>> combos = new ArrayList<>();
        combos.add(new LinkedHashMap<>());
        for (int i = 0; i < attrNames.size(); i++) {
            String attrName = attrNames.get(i);
            String[] values = valueArrays.get(i);
            List<Map<String, String>> next = new ArrayList<>();
            for (Map<String, String> combo : combos) {
                for (String value : values) {
                    Map<String, String> newCombo = new LinkedHashMap<>(combo);
                    newCombo.put(attrName, value);
                    next.add(newCombo);
                }
            }
            combos = next;
        }

        // 3. 封装为SkuRow
        List<SkuGenerateVO.SkuRow> rows = new ArrayList<>();
        for (Map<String, String> combo : combos) {
            SkuGenerateVO.SkuRow row = new SkuGenerateVO.SkuRow();
            row.setAttrValues(combo);
            row.setSkuName(String.join(" ", combo.values()));
            row.setEnabled(true);
            row.setDiscounts(new ArrayList<>());
            row.setReductions(new ArrayList<>());
            row.setAlbumImages(new String[0]);
            rows.add(row);
        }
        return rows;
    }
}
