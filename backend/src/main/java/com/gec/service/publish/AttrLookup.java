package com.gec.service.publish;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gec.dao.GoodsAttrMapper;
import com.gec.domain.entity.GoodsAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 属性批量查询模块，用于消除 persistAll 中的 N+1 查询。
 */
@Service
public class AttrLookup {

    @Autowired
    private GoodsAttrMapper goodsAttrMapper;

    /**
     * 按 id 批量加载属性名称。
     *
     * @param attrIds 属性 id 集合
     * @return Map<attrId, attrName>
     */
    public Map<Integer, String> loadAttrNamesById(Collection<Integer> attrIds) {
        if (attrIds == null || attrIds.isEmpty()) {
            return new HashMap<>();
        }
        List<GoodsAttr> attrs = goodsAttrMapper.selectBatchIds(attrIds);
        Map<Integer, String> map = new HashMap<>();
        for (GoodsAttr attr : attrs) {
            map.put(attr.getId(), attr.getAttrName());
        }
        return map;
    }

    /**
     * 按属性名称加载销售属性 id。
     * 重名时 id 更大（更新）的会覆盖 id 更小的，避免旧占位数据干扰。
     *
     * @param categoryId 分类 id，可为 null（不限分类）
     * @return Map<attrName, id>
     */
    public Map<String, Integer> loadSaleAttrIdsByName(Integer categoryId) {
        QueryWrapper<GoodsAttr> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type", 2);
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        wrapper.orderByDesc("id").last("limit 100");
        List<GoodsAttr> attrs = goodsAttrMapper.selectList(wrapper);
        Map<String, Integer> map = new HashMap<>();
        for (GoodsAttr attr : attrs) {
            // id 降序遍历，重名时后面的（id 更大的）会覆盖前面的
            map.put(attr.getAttrName(), attr.getId());
        }
        return map;
    }

}
