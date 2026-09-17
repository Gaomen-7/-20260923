package com.gec.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.domain.entity.GoodsAttrGroup;
import com.gec.service.IAttrGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/AttrGroup")
public class AttrGroupController extends BaseController {
    @Autowired
    private IAttrGroupService attrGroupService;

    /* 1.分页查询属性分组列表 */
    @PostMapping(
            value="/list/{page}/{limit}/{categoryId}",
            produces="application/json;charset=UTF-8"
    )
    public R list(
            @PathVariable("page") Integer page,
            @PathVariable("limit") Integer limit,
            @PathVariable("categoryId") Integer categoryId){
        try{
            Page frmPage = newPage(page, limit);
            return R.convertPage(attrGroupService.listAttrGroup(frmPage, categoryId));
        }catch(Exception e){
            e.printStackTrace();
            return R.err(e);
        }
    }

    /* 2.新增属性分组 */
    @PostMapping(
            value="/add",
            produces="application/json;charset=UTF-8"
    )
    public R add(@RequestBody GoodsAttrGroup attrGroup){
        try{
            attrGroupService.addAttrGroup(attrGroup);
            return R.ok();
        }catch(Exception e){
            e.printStackTrace();
            return R.err(e);
        }
    }

    /* 3.修改属性分组 */
    @PutMapping(
            value="/update",
            produces="application/json;charset=UTF-8"
    )
    public R update(@RequestBody GoodsAttrGroup attrGroup){
        try{
            attrGroupService.updateAttrGroup(attrGroup);
            return R.ok();
        }catch(Exception e){
            e.printStackTrace();
            return R.err(e);
        }
    }

    /* 4.删除属性分组 */
    @DeleteMapping(
            value="/delete/{id}",
            produces="application/json;charset=UTF-8"
    )
    public R delete(@PathVariable("id") Integer id){
        try{
            attrGroupService.deleteAttrGroup(id);
            return R.ok();
        }catch(Exception e){
            e.printStackTrace();
            return R.err(e);
        }
    }

    @ExceptionHandler(Exception.class)
    public R exceptionFallback(Exception E){
        E.printStackTrace();
        return R.err(E);
    }

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }
}
