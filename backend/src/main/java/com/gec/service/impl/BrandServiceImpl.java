package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.BrandMapper;
import com.gec.domain.entity.Brand;
import com.gec.domain.search.BrandSearch;
import com.gec.domain.vo.BrandCategoryVO;
import com.gec.domain.vo.BrandVO;
import com.gec.service.IBrandService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
 类格式如下:
   {1}@Service 注解
   {2}extends 【Service通用实现】
   {3}implements 【Service接口】
   {4}自动装配 【当前映射器】
*/
@Service
public class BrandServiceImpl
    extends ServiceImpl<BrandMapper, Brand>
    implements IBrandService {
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public IPage<Brand> listBrand(
        Page page, BrandSearch param ) {
        /*--创建一个条件设置器--*/
        QueryWrapper<Brand>QW = new QueryWrapper<>();
        /*动态设置查询条件*/
        if (param.getBrandName()!=null){
            QW.like("brand_name",param.getBrandName());
        }
        if (param.getId()!=null){
            QW.like("id",param.getId());
        }
        /*根据查询器条件进行记录检索*/
        Page retPage = brandMapper.selectPage(page,QW);
        /*返回查询结果*/
        return retPage;
    }

    /*
    * 以下代码用于关联品牌与类别的数据关系。
    * * 会在 tbl_brand_category 添加相应的数据。
    */
    @Override
    public void associateCategory(BrandCategoryVO bcVO) {
        /*--在做类别关联时，先删除原来的关联数据--*/
        brandMapper.removeAssociate(
                bcVO.getBrandId(),bcVO.getDeleteIds()
        );
        /*拿到要关联的类别数量*/
        int SIZE = bcVO.getCategories().size();
        /*如果有类别数据*/
        if (SIZE>0){
            /*插入关联的数据*/
            int cnt = brandMapper.associateCategory(bcVO);
            /*判断写入实际数量是否一致*/
            if(cnt!=SIZE){
                throw new RuntimeException("设置品牌与类别关联失败");
            }
        }
    }

    @Override
    public IPage<BrandVO> getListByCategory
        ( Page page, BrandSearch param ) {
        /*--填入代码(3)--*/
        return  brandMapper.listByCategory(page,param);
    }

    @Override
    public void addBrand(Brand brand) { }

    @Override
    public void updateBrand(Brand brand) { }

}
