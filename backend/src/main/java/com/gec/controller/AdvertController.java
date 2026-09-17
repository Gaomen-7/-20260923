package com.gec.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.domain.entity.Advert;
import com.gec.domain.search.AdvertSearch;
import com.gec.domain.vo.UploadResultVO;
import com.gec.service.IAdvertService;
import com.gec.service.publish.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Advert")
public class AdvertController extends BaseController {

    @Autowired
    private IAdvertService advertService;

    @Autowired
    private FileUploadService fileUploadService;

    /* 1. 分页查询广告列表 */
    @PostMapping("/list/{page}/{limit}")
    public R list(
            @PathVariable("page") Integer page,
            @PathVariable("limit") Integer limit,
            @RequestBody AdvertSearch param) {
        Page frmPage = newPage(page, limit);
        IPage<Advert> retPage = advertService.pageAdvert(frmPage, param);
        return R.convertPage(retPage);
    }

    /* 2. 新增广告 */
    @PostMapping("/add")
    public R add(@RequestBody Advert advert) {
        advertService.addAdvert(advert);
        return R.ok();
    }

    /* 3. 更新广告 */
    @PutMapping("/update")
    public R update(@RequestBody Advert advert) {
        advertService.updateAdvert(advert);
        return R.ok();
    }

    /* 4. 删除单个广告 */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id") Integer id) {
        boolean ret = advertService.removeById(id);
        if (!ret) {
            throw new RuntimeException("删除失败");
        }
        return R.ok();
    }

    /* 5. 批量删除 */
    @PostMapping("/batchDelete")
    public R batchDelete(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("ids");
        advertService.batchDelete(ids);
        return R.ok();
    }

    /* 6. 上下线切换 */
    @PostMapping("/toggleStatus")
    public R toggleStatus(@RequestBody Map<String, Object> body) {
        Integer id = body.get("id") != null ? Integer.valueOf(body.get("id").toString()) : null;
        Integer status = body.get("status") != null ? Integer.valueOf(body.get("status").toString()) : null;
        advertService.toggleStatus(id, status);
        return R.ok();
    }

    /* 刷新过期广告状态（将已过期且仍在投放中的广告批量下线） */
    @PostMapping("/refreshExpired")
    public R refreshExpired() {
        int count = advertService.refreshExpired();
        return R.ok().put("count", count);
    }

    /* 曝光量+1 */
    @PostMapping("/incrementView/{id}")
    public R incrementView(@PathVariable("id") Integer id) {
        advertService.incrementView(id);
        return R.ok();
    }

    /* 点击量+1 */
    @PostMapping("/incrementClick/{id}")
    public R incrementClick(@PathVariable("id") Integer id) {
        advertService.incrementClick(id);
        return R.ok();
    }

    /* 7. 广告图片上传 */
    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile mFile) {
        UploadResultVO result = fileUploadService.upload(mFile, "advert", "/Advert/showImg/");
        return R.ok().put("logoUri", result.getLogoUri()).put("fileName", result.getFileName());
    }

    /* 8. 广告图片展示 */
    @GetMapping("/showImg/{imgName}")
    public void showImg(@PathVariable("imgName") String imgName, HttpServletResponse resp) {
        try {
            byte[] data = fileUploadService.getFileBytes("advert", imgName);
            resp.setContentType(getMIME(imgName));
            resp.getOutputStream().write(data);
        } catch (Exception e) {
            send404File(resp);
        }
    }

    @ExceptionHandler(Exception.class)
    public void exceptionFallback(Exception E, HttpServletResponse resp) {
        E.printStackTrace();
        R.err(E).write(resp);
    }

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }
}
