package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.domain.entity.Brand;
import com.gec.domain.search.BrandSearch;
import com.gec.domain.vo.BrandCategoryVO;
import com.gec.domain.vo.BrandVO;
import com.gec.domain.vo.OptionVO;
import com.gec.domain.vo.UploadResultVO;
import com.gec.service.IBrandService;
import com.gec.service.publish.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Brand")
public class BrandController extends BaseController {
    @Autowired
    private IBrandService brandService;

    @Autowired
    private FileUploadService fileUploadService;

    /* 1.获取列表。【POST请求】 */
    @PostMapping(value = "/list/{page}/{limit}")
    public R list(
            @PathVariable("page")Integer page,
            @PathVariable("limit")Integer limit,
            @RequestBody BrandSearch param){
            /*调用父类方法来参数检查与封装*/
            Page frmPage = newPage(page,limit);
            /*调用service方法实现查询（有搜索，分页）*/
            IPage<Brand> retPage = brandService.listBrand(
                    frmPage,param
            );
            return R.convertPage(retPage);
    }

    /*
        2.设置状态。
          设置其是否在商城的主页中显示出来。
    */
    @PostMapping(value = "")
    public R setStatus(@RequestBody Brand brand){
        /*创建一个条件设置器（更新专用的）*/
        UpdateWrapper<Brand>UW = new UpdateWrapper<>();
        /*动态设置条件与修改数据*/
        UW.eq("id",brand.getId());
        UW.eq("show_status",brand.getShowStatus());
        /*调用内置方法去更新数据*/
        boolean ret = brandService.update(UW);
        if (ret){
            return R.ok();
        }else {
            throw new RuntimeException("设置品牌显示状态失败");
        }
    }

    /* 3.添加品牌。*/
    @PostMapping("/addBrand")
    public R addBrand(@RequestBody Brand brand){
        boolean ret = brandService.save(brand);
        if (!ret){
            throw new RuntimeException("添加品牌失败");
        }
        return R.ok();
    }
    /* 4.更新品牌。*/
    @PutMapping("/updateBrand")
    public R updateBrand(@RequestBody Brand brand){
        UpdateWrapper<Brand>UW = new UpdateWrapper<>();
        UW.eq("id",brand.getId());
        boolean ret = brandService.update(brand,UW);
        if (!ret){
            throw new RuntimeException("更新品牌失败");
        }
        return R.ok();
    }
    /* 5.关联类别。*/

    //6.文件上传。

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }

    /*
    * 此方法用于读取本地的一张图片。
    * 把数据输出到请求端（浏览器）。
    * 浏览器即能展示出来。
    */
    @GetMapping("/showImg/{imgName}")
    public void showImg(
            @PathVariable("imgName")String imgName,
            HttpServletResponse resp){
        try{
            /*
            * brand = 'brand'
            * imgName = '01.png'
            * 图片输出到==》浏览器
             */
            byte[] data = fileUploadService.getFileBytes("brand", imgName);
            resp.setContentType(getMIME(imgName));
            resp.getOutputStream().write(data);
        } catch (Exception e) {
            //图片不存在，发送404图片
            send404File(resp);
        }
    }
    /*
    * upload()。
    */
    @PostMapping("/upload")
    public R upload(
            @RequestParam("file")MultipartFile mFile,
            @RequestParam("userId")Integer userId
    ){
        UploadResultVO result = fileUploadService.upload(mFile, "brand", "/Brand/showImg/");
        return R.ok().put("logoUri", result.getLogoUri()).put("fileName", result.getFileName());
    }

    /*
     * listByCategory()。
     */
    @PostMapping("/listByCategory/{page}/{limit}")
    public R listByCategory(
            @PathVariable("page")Integer page,
            @PathVariable("limit")Integer limit,
            @RequestBody BrandSearch param){
            Page frmPage = newPage(page,limit);
            /*调用service方法获取列表*/
            IPage<BrandVO> retPage = brandService.getListByCategory(frmPage,param);
            /*把页对象转化为指定的JSON格式*/
            return R.convertPage(retPage);
    }

    /*
     * brandOptions()。
     * 根据类别ID查询关联的品牌选项。
     */
    @GetMapping("/brandOptions/{categoryId}")
    public R brandOptions(
        @PathVariable("categoryId") Integer categoryId) {
        return R.ok(brandService.brandOptions(categoryId));
    }
}
