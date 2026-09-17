package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.AttrGroupMapper;
import com.gec.domain.entity.GoodsAttrGroup;
import com.gec.service.IAttrGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttrGroupServiceImpl
    extends ServiceImpl<AttrGroupMapper, GoodsAttrGroup>
    implements IAttrGroupService {
    @Autowired
    private AttrGroupMapper attrGroupMapper;
    /*获取属性分组列表*/
    public IPage listAttrGroup(Page page,Integer categoryId){
        /*使用Lambda表达式条件设置器*/
        LambdaQueryWrapper<GoodsAttrGroup>QW =new LambdaQueryWrapper<>();
        /*相当于生成：WHERE category_id=入参值*/
        QW.eq(GoodsAttrGroup::getCategoryId,categoryId);
        /*调用Mybaits-Plus*/
        return attrGroupMapper.selectPage(page,QW);
    }
    /*添加属性分组*/
    @Override
    public void addAttrGroup(GoodsAttrGroup attrGroup){
        /*使用Mybaits-Plus的映射器的内置方法*/
        int cnt = attrGroupMapper.insert(attrGroup);
        if(cnt!=1){
            throw new RuntimeException("添加属性失败");
        }
    }
    /*更新属性分组 */
    @Override
    public void updateAttrGroup(GoodsAttrGroup attrGroup){
        /*使用Lambda表达式条件设置器*/
        LambdaQueryWrapper<GoodsAttrGroup>UW = new LambdaQueryWrapper<>();
        /*相当于生成：WHERE category_id=入参值*/
        UW.eq(GoodsAttrGroup::getId,attrGroup.getId());
        int cnt = attrGroupMapper.update(attrGroup,UW);
        if(cnt!=1){
            throw new RuntimeException("更新属性分组失败");
        }
    }

    /*删除属性分组*/
    @Override
    public void deleteAttrGroup(Integer id){
        int cnt = attrGroupMapper.deleteById(id);
        if(cnt!=1){
            throw new RuntimeException("删除属性分组失败");
        }
    }

}
