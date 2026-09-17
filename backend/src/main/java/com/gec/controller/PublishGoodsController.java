package com.gec.controller;

import com.gec.components.FileTemplate;
import com.gec.domain.entity.GoodsAttrValue;
import com.gec.domain.vo.GoodsAttrValuesVO;
import com.gec.domain.vo.GoodsBaseInfoVO;
import com.gec.domain.vo.SkuGenerateVO;
import com.gec.domain.vo.UploadResultVO;
import com.gec.service.publish.FileUploadService;
import com.gec.service.publish.GoodsPersister;
import com.gec.service.publish.PublishSessionStore;
import com.gec.service.publish.SkuGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/PublishGoods")
public class PublishGoodsController extends BaseController {

    @Autowired
    private SkuGenerator skuGenerator;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private PublishSessionStore sessionStore;
    @Autowired
    private GoodsPersister goodsPersister;

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }

    /*
    *   此方法用于读取本地的一张图片。
    *   把数据输出到请求端（浏览器）。
    *   浏览器即能展示出来。
    *   http://localhost:8090/mall-sys/PublishGoods/showImg/01.png
    */
    @GetMapping("/showImg/{dir}/{imgName}")
    public void showImg(
        @PathVariable("dir")String dir,
        @PathVariable("imgName")String imgName,
        HttpServletResponse resp ){
        try{
            byte[] data = fileUploadService.getFileBytes(dir, imgName);
            resp.setContentType(getMIME(imgName));
            resp.getOutputStream().write(data);
        }catch (Exception e){
            send404File(resp);
        }
    }

    /*
    * upload() 文件上传的方法。
    */
    @PostMapping("/upload")
    public R upload(
        @RequestParam("file")MultipartFile mFile,
        @RequestParam("type")Integer type ){
        /* 1.判断上传的文件是否为空。 */
        boolean isEmpty = mFile.isEmpty();
        if( isEmpty ){
            throw new RuntimeException("文件不能为空。");
        }
        /* 2.如果类型是 1, 表示我上传的是主图片。 */
        String subDir = type.equals(1) ? "goods" : "album";
        /* 3.委托 FileUploadService 完成改名、落盘、拼 URI。 */
        UploadResultVO result =
            fileUploadService.upload(mFile, subDir, "/PublishGoods/showImg/");
        /* 4.把图片名, uri 返回给前端。
         * uri 用来显示用的, 文件名用来保存数据库的。 */
        return R.ok()
            .put("logoUri", result.getLogoUri())
            .put("fileName", result.getFileName());
    }

    /*
    * saveGoodsBaseInfo() 保存商品基本信息到Redis缓存。
    */
    @PostMapping("/saveGoodsBaseInfo")
    public R saveGoodsBaseInfo(
        @RequestBody GoodsBaseInfoVO biVO ) {
        String pubKey = sessionStore.generatePubKey();
        sessionStore.save(pubKey, PublishSessionStore.PublishStep.BASE, biVO);
        return R.ok()
            .put("pubKey",pubKey);
    }

    /*
    * 2.保存规格参数设置(发布商品)
    */
    @PostMapping("/saveGoodsAttrValues")
    public R saveGoodsAttrValues(
        @RequestBody GoodsAttrValuesVO gavVO ) {
        sessionStore.save(gavVO.getPubKey(),
            PublishSessionStore.PublishStep.GOODS_ATTR, gavVO.getAttrList());
        return R.ok()
            .put("pubKey", gavVO.getPubKey());
    }

    /*
    * 3.保存销售属性设置(发布商品)
    */
    @PostMapping("/saveSaleAttrValues")
    public R saveSaleAttrValues(
        @RequestBody GoodsAttrValuesVO gavVO ) {
        sessionStore.save(gavVO.getPubKey(),
            PublishSessionStore.PublishStep.SALE_ATTR, gavVO.getAttrList());
        return R.ok()
            .put("pubKey", gavVO.getPubKey());
    }

    /*
    * 4.读取销售属性选中值(发布商品 STEP04 回显)
    *   从 Redis 读取 {pubKey}-sale-attr。
    */
    @GetMapping("/getSaleAttr")
    public R getSaleAttr(
        @RequestParam("pubKey") String pubKey ) {
        List<GoodsAttrValue> attrList =
            (List<GoodsAttrValue>) sessionStore.get(pubKey,
                PublishSessionStore.PublishStep.SALE_ATTR);
        return R.ok()
            .put("attrList", attrList);
    }

    /*
    * 5.读取图集图片文件名列表(发布商品 STEP04 回显)
    *   从 Redis 读取 {pubKey}-base，取 spuAlbumVO.images。
    *   若无数据返回空数组。
    *   同时返回 categoryId 用于前端获取属性模板。
    */
    @GetMapping("/getAlbumList")
    public R getAlbumList(
        @RequestParam("pubKey") String pubKey ) {
        String[] albumList = new String[0];
        Integer categoryId = null;
        Object baseInfo = sessionStore.get(pubKey,
            PublishSessionStore.PublishStep.BASE);
        if( baseInfo instanceof GoodsBaseInfoVO ){
            GoodsBaseInfoVO biVO = (GoodsBaseInfoVO) baseInfo;
            if( biVO.getSpuAlbumVO()!=null
                && biVO.getSpuAlbumVO().getImages()!=null ){
                albumList = biVO.getSpuAlbumVO().getImages();
            }
            if( biVO.getGoodsDetail()!=null ){
                categoryId = biVO.getGoodsDetail().getCategoryId();
            }
        }
        return R.ok()
            .put("albumList", albumList)
            .put("categoryId", categoryId);
    }

    /*
    * 6.根据销售属性选中值生成SKU组合(笛卡尔积)
    *   每个 attrItem 的 attrValue 按分号拆分，
    *   做笛卡尔积，每种组合封装为一个 SkuRow。
    */
    @PostMapping("/generateSku")
    public R generateSku(
        @RequestBody SkuGenerateVO skuVO ) {
        List<SkuGenerateVO.SkuRow> skuRows =
            skuGenerator.generate( skuVO.getAttrItems() );
        skuVO.setSkuRows( skuRows );
        return R.ok()
            .put("skuVO", skuVO)
            .put("skuRows", skuRows);
    }

    /*
    * 7.保存SKU信息到Redis缓存(发布商品 STEP04)
    *   接收 {pubKey, skuData}，写入 {pubKey}-sku。
    */
    @PostMapping("/saveSkuCache")
    public R saveSkuCache(
        @RequestBody Map<String,Object> param ) {
        String pubKey = (String) param.get("pubKey");
        Object skuData = param.get("skuData");
        if( pubKey==null || pubKey.trim().isEmpty() ){
            throw new RuntimeException("pubKey不能为空。");
        }
        sessionStore.save(pubKey, PublishSessionStore.PublishStep.SKU, skuData);
        return R.ok();
    }

    /*
    * 8.保存完成：Redis四步数据持久化到数据库(发布商品 STEP05)
    *   接收 pubKey，写入5张表，返回 goodsId，并清理Redis缓存。
    */
    @PostMapping("/saveComplete")
    public R saveComplete(
        @RequestParam("pubKey") String pubKey ) {
        Integer goodsId = null;
        try {
            goodsId = goodsPersister.persist( pubKey );
        } finally {
            try {
                sessionStore.clear( pubKey );
            } catch (Exception e) {
                /* ignore */
            }
        }
        return R.ok()
            .put("goodsId", goodsId);
    }

}
