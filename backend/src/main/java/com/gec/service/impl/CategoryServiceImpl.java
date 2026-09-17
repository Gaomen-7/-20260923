package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.CategoryMapper;
import com.gec.domain.bo.CategoryBO;
import com.gec.domain.entity.Brand;
import com.gec.domain.entity.Category;
import com.gec.domain.entity.Node;
import com.gec.domain.vo.BrandVO;
import com.gec.domain.vo.CategoryBrandVO;
import com.gec.service.IBaseService;
import com.gec.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 {PS}类格式如下:
   {1}@Service 注解
   {2}extends 【Service通用实现】
   {3}implements 【当前Service接口】【IBaseService接口】
   {4}自动装配 【当前映射器】
*/

@Service
public class CategoryServiceImpl
    extends ServiceImpl<CategoryMapper, Category>
    implements ICategoryService, IBaseService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Node> listCategory() {
       /* ..填入代码.. */
        QueryWrapper<Category> QW= new QueryWrapper<>();
        /*从数据库中得到原始数据*/
        List<Category> list= categoryMapper.selectList(QW);
        /**/
        List<Node> nodes = convertNodeBO(list);
        /*返回一级类别列表*/
        return nodes;
    }

    @Override
    public Node copyObj(Node node) {
        Category C = (Category) node;
        /* ..获取节点的父ID的序列.. */
        String pIds = C.getPIds();
        /*把它升级为业务对象BO*/
        CategoryBO BO= new CategoryBO(C);
        /*计算你位于第几层*/
        String[] sp= pIds.split(",");
        int LEVEL = sp.length;
        /*保存层级数*/
        BO.setLevel(LEVEL);
        return BO;

    }

    /*
    * VUE 前端需要我们提供一个这样的数组。
    * 如: 得到 "新类别名称38" 的 id 序列。
    * ID: 38
    * PIDS: [ 0, 1, 10, 12 ]
    * 把 0 去掉, 把 38 加上。
    * 得到数组: [1], [10], [12], [38]
    */
    @Override
    public Integer[] getPidsArr(Integer id) {
        /* ..填入代码.. */
        String pids = categoryMapper.getPids(id);
        pids = pids.replaceAll("^0","") +id;
        String[] arr = pids.split(",");
        Integer[] i_arr = new Integer[arr.length];
        for(int j=0;j<arr.length;j++){
            i_arr[j] = Integer.valueOf(arr[j]);
        }
        return i_arr;
    }

    @Override
    public String getPids(Integer id) {
        return categoryMapper.getPids( id );
    }

    /*
    * CategoryServiceImpl.java
    * 功能: 用于关联类别与品牌的。
    */
    @Override
    public void associateBrand(CategoryBrandVO cbVO)
    {
        /* 删除原有的关联数据 */
        Integer categoryId = cbVO.getCategoryId();
        List<Integer> brandIds = cbVO.getDeleteIds();
        categoryMapper.removeAssociate(categoryId,brandIds);
        /*前端传过来的数据有没有新关联的品牌信息，如果有则设置关联*/
        int SIZE = cbVO.getBrands().size();
        if (SIZE>0){
            /*插入关联数据（类别对品牌）*/
            int cnt = categoryMapper.associateBrand(cbVO);
            /*更新数对不上，说明有记录没有插入成功*/
            if(cnt!=SIZE){
                throw new RuntimeException("设置类别对品牌关联失败");
            }

        }

    }

    @Override
    public IPage<BrandVO> getListByBrand(Page page, Map data) {
        return null;
    }

}
