package com.gec.controller;

/*
* 控制器格式:
* 1.@RestController
* 2.@ReqeustMapping(父级映射地址)
* 3.extends BaseController (仅此项目需要)
* 4.自动装配 当前模组的服务接口。
* 5.编写相关的 WEB 接口方法。
* 6.待定..
*/

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.domain.entity.Category;
import com.gec.domain.entity.Node;
import com.gec.domain.vo.BrandVO;
import com.gec.domain.vo.CategoryBrandVO;
import com.gec.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Category")
public class CategoryController extends BaseController {
    //{PS}自动装配:当前模组的服务接口。
    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private FileTemplate fileTemplate;

    /* 1.列表接口. */
    @GetMapping("/list")
    public R list(){
        List<Node> nodes = null;
        /*调用SERVICE方法获取树形列表*/
        nodes = categoryService.listCategory();
        /*封装到R对象中，输送给前端*/
        return R.ok(nodes);
    }

    /* 2.添加类别. */
    @PostMapping("/addCategory")
    public R addCategory(
        @RequestBody Category category){
        /*获取父ID*/
        Integer parID = category.getParentId();
        /*获取父节点的ID序列*/
        String pids = categoryService.getPids(parID);
        /*手动拼接PIDS*/
        category.setPIds(pids +","+parID);
        /*插入类别到数据表*/
        boolean ret = categoryService.save(category);
        if (!ret){
            throw new RuntimeException("添加类别失败");
        }
        return R.ok();
    }

    /* 3.更新类别. */
    @PostMapping("/updateCategory")
    public R updateCategory(
        @RequestBody Category category) {

        Integer parID = category.getParentId();

        String pids = categoryService.getPids(parID);

        category.setPIds(pids + "," + parID);

        /*创建条件与更新数据设置器（更新用的）*/
        UpdateWrapper<Category> UW = new UpdateWrapper<>();
        UW.eq("id", category.getId());
        boolean ret = categoryService.update(category, UW);
        if (!ret) {
            throw new RuntimeException("更新类别失败");
        }
        return R.ok();
    }

    /* 4.获取类别. */
    @GetMapping("/getCategory/{id}")
    public R getCategory(
       @PathVariable("id")Integer id){
         Category category = categoryService.getById(id);
         return R.ok(category);
    }

    /* 5.删除类别. */
    @DeleteMapping("/deleteCategory/{id}")
    public R deleteCategory(
        @PathVariable("id")Integer id){
        /*检查该类别下是否有子类别*/
        QueryWrapper<Category> qw = new QueryWrapper<>();
        qw.eq("parent_id", id);
        int childCount = categoryService.count(qw);
        if(childCount > 0){
            throw new RuntimeException("该类别下存在子类别，无法删除");
        }
        /*执行删除*/
        boolean ret = categoryService.removeById(id);
        if (!ret){
            throw new RuntimeException("删除类别失败");
        }
        return R.ok();
    }

    /* 6.关联品牌 */
    @PostMapping("/associateBrand")
    public R associateBrand(
        @RequestBody CategoryBrandVO cbVO){
        categoryService.associateBrand(cbVO);
        return R.ok();
    }

    /* 7.获取 IDS 序列数组。*/
    @GetMapping("/getPids/{id}")
    public R getPids(@PathVariable("id")Integer id){
        Integer[] ids = {};
        ids = categoryService.getPidsArr(id);
        return R.ok(ids);
    }

    /* 8.获取类别列表(带关联标记). */
    @PostMapping("/listByBrand/{page}/{limit}")
    public R listByBrand(
       @PathVariable("page")Integer page,
       @PathVariable("limit")Integer limit,
       @PathVariable Map data){
       Page frmPage = newPage(page,limit);
       IPage<BrandVO>retPage = categoryService.getListByBrand(frmPage,data);
       return R.convertPage(retPage);
    }

    @ExceptionHandler
    public void exceptionHandler(
            Exception e, HttpServletResponse resp){
        e.printStackTrace();
        R.err( e ).write( resp );
    }

    @Override
    protected FileTemplate getFileTemplate() {
        return fileTemplate;
    }
}
