package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.GoodsAttrMapper;
import com.gec.domain.bo.GoodsAttrBO;
import com.gec.domain.entity.GoodsAttr;
import com.gec.domain.search.GoodsAttrSearch;
import com.gec.domain.vo.GoodsAttrVO;
import com.gec.service.IGoodsAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoodsAttrServiceImpl
    extends ServiceImpl<GoodsAttrMapper, GoodsAttr>
    implements IGoodsAttrService {
    @Autowired
    private GoodsAttrMapper goodsAttrMapper;

    /* 【A】分页查询属性列表（关联分组名称） */
    @Override
    public IPage<GoodsAttrBO> listGoodsAttr(
            Page page, GoodsAttrSearch attrSearch) {
        return goodsAttrMapper.getGoodsAttrList(page, attrSearch);
    }

    /* 【B】新增属性（同时维护分组关联） */
    @Override
    @Transactional
    public void addGoodsAttr(GoodsAttrVO attrVO) {
        GoodsAttr attr = getEntity(attrVO);
        if (attr.getEnable() == null) {
            attr.setEnable(1);
        }
        if (attr.getSearchEnable() == null) {
            attr.setSearchEnable(0);
        }
        boolean saved = this.save(attr);
        if (!saved) {
            throw new RuntimeException("添加属性失败");
        }
        /* 维护分组关联 */
        if (attrVO.getAttrGroupId() != null) {
            goodsAttrMapper.addGroupAttrRelation(
                attrVO.getAttrGroupId(), attr.getId());
        }
    }

    /* 【C】更新属性（先删后插分组关联） */
    @Override
    @Transactional
    public void updateGoodsAttr(GoodsAttrVO attrVO) {
        GoodsAttr attr = getEntity(attrVO);
        boolean updated = this.updateById(attr);
        if (!updated) {
            throw new RuntimeException("更新属性失败");
        }
        /* 重新维护分组关联：先删后插 */
        goodsAttrMapper.removeGroupAttrRelation(attrVO.getId());
        if (attrVO.getAttrGroupId() != null) {
            goodsAttrMapper.addGroupAttrRelation(
                attrVO.getAttrGroupId(), attrVO.getId());
        }
    }

    /* 【D】删除属性（同时删除分组关联） */
    @Override
    @Transactional
    public void deleteGoodsAttr(Integer id) {
        goodsAttrMapper.removeGroupAttrRelation(id);
        boolean removed = this.removeById(id);
        if (!removed) {
            throw new RuntimeException("删除属性失败");
        }
    }

    /* 【发布商品专用】根据类别+属性类型查询启用的属性 */
    @Override
    public List<GoodsAttr> queryGoodsAttrByCategory(
        Integer categoryId, Integer attrType) {
        QueryWrapper<GoodsAttr> QW = new QueryWrapper<>();
        QW.eq("category_id", categoryId)
          .eq("attr_type", attrType)
          .eq("enable", 1)
          .orderByAsc("id");
        return goodsAttrMapper.selectList(QW);
    }

    /* VO → Entity 转换 */
    private GoodsAttr getEntity(GoodsAttrVO attrVO) {
        GoodsAttr attr = new GoodsAttr();
        attr.setId(attrVO.getId());
        attr.setAttrName(attrVO.getAttrName());
        attr.setCategoryId(attrVO.getCategoryId());
        attr.setAttrType(attrVO.getAttrType());
        attr.setValueType(attrVO.getValueType());
        attr.setAttrValue(attrVO.getAttrValue());
        attr.setEnable(attrVO.getEnable());
        attr.setSearchEnable(attrVO.getSearchEnable());
        return attr;
    }
}
