package com.gec.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.domain.bo.GoodsAttrBO;
import com.gec.domain.entity.GoodsAttr;
import com.gec.domain.search.GoodsAttrSearch;
import com.gec.domain.vo.GoodsAttrVO;
import com.gec.service.IAttrGroupService;
import com.gec.service.IGoodsAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/GoodsAttr")
public class GoodsAttrController extends BaseController {
    @Autowired
    private IGoodsAttrService goodsAttrService;

    @Autowired
    private IAttrGroupService attrGroupService;

    /* 【1】分页查询属性列表（支持按分类/类型/名称/分组筛选） */
    @PostMapping("/list/{page}/{limit}")
    public R list(
            @PathVariable("page") Integer page,
            @PathVariable("limit") Integer limit,
            @RequestBody GoodsAttrSearch param) {
        Page frmPage = newPage(page, limit);
        IPage<GoodsAttrBO> retPage = goodsAttrService.listGoodsAttr(
            frmPage, param);
        return R.convertPage(retPage);
    }

    /* 【2】新增属性 */
    @PostMapping("/add")
    public R add(@RequestBody GoodsAttrVO attrVO) {
        goodsAttrService.addGoodsAttr(attrVO);
        return R.ok();
    }

    /* 【3】更新属性 */
    @PutMapping("/update")
    public R update(@RequestBody GoodsAttrVO attrVO) {
        goodsAttrService.updateGoodsAttr(attrVO);
        return R.ok();
    }

    /* 【4】删除属性 */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id") Integer id) {
        goodsAttrService.deleteGoodsAttr(id);
        return R.ok();
    }

    /* 【5】根据类别+类型查询属性（发布商品专用） */
    @GetMapping("/listByCategory/{categoryId}/{attrType}")
    public R listByCategory(
        @PathVariable("categoryId") Integer categoryId,
        @PathVariable("attrType") Integer attrType) {
        List<GoodsAttr> list = goodsAttrService
            .queryGoodsAttrByCategory(categoryId, attrType);
        return R.ok(list);
    }

    /* 【6】查询某分类下的属性分组列表（下拉选项用） */
    @GetMapping("/groupOptions/{categoryId}")
    public R groupOptions(
        @PathVariable("categoryId") Integer categoryId) {
        return R.ok(attrGroupService.listGroupsByCategory(categoryId));
    }

    @ExceptionHandler(Exception.class)
    public void exceptionFallback(
        Exception E, HttpServletResponse resp) {
        E.printStackTrace();
        R.err(E).write(resp);
    }

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }
}
