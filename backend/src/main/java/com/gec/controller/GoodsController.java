package com.gec.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gec.components.FileTemplate;
import com.gec.domain.entity.GoodsInfo;
import com.gec.domain.entity.SkuInfo;
import com.gec.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Goods")
public class GoodsController extends BaseController {
    @Autowired
    private IGoodsService goodsService;

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }

    /*
     * 商品列表（分页+筛选）
     * GET /Goods/list?page=1&limit=10&categoryId=&brandId=&publishStatus=&isRecommend=&keyword=
     */
    @GetMapping("/list")
    public R list(
        @RequestParam(value="page", defaultValue="1") Integer page,
        @RequestParam(value="limit", defaultValue="10") Integer limit,
        @RequestParam(value="categoryId", required=false) Integer categoryId,
        @RequestParam(value="brandId", required=false) Integer brandId,
        @RequestParam(value="publishStatus", required=false) Integer publishStatus,
        @RequestParam(value="isRecommend", required=false) Integer isRecommend,
        @RequestParam(value="keyword", required=false) String keyword,
        @RequestParam(value="minPrice", required=false) BigDecimal minPrice,
        @RequestParam(value="maxPrice", required=false) BigDecimal maxPrice,
        @RequestParam(value="goodsSn", required=false) String goodsSn,
        @RequestParam(value="lowStock", required=false) Integer lowStock) {

        IPage<GoodsInfo> pageResult = goodsService.pageGoods(page, limit, categoryId, brandId, publishStatus, isRecommend, keyword, minPrice, maxPrice, goodsSn, lowStock);
        return R.ok()
            .put("data", pageResult.getRecords())
            .put("total", pageResult.getTotal());
    }

    /*
     * 上架/下架切换
     * POST /Goods/updateStatus?id=1&publishStatus=1
     */
    @PostMapping("/updateStatus")
    public R updateStatus(
        @RequestParam("id") Integer id,
        @RequestParam("publishStatus") Integer publishStatus) {
        goodsService.updateStatus(id, publishStatus);
        return R.ok();
    }

    /*
     * 批量上架/下架
     * POST /Goods/batchUpdateStatus  body: {ids:[1,2,3], publishStatus:1}
     */
    @PostMapping("/batchUpdateStatus")
    public R batchUpdateStatus(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("ids");
        Integer publishStatus = Integer.valueOf(body.get("publishStatus").toString());
        goodsService.batchUpdateStatus(ids, publishStatus);
        return R.ok();
    }

    /*
     * 批量删除
     * POST /Goods/batchDelete  body: {ids:[1,2,3]}
     */
    @PostMapping("/batchDelete")
    public R batchDelete(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("ids");
        goodsService.batchDelete(ids);
        return R.ok();
    }

    /*
     * 切换推荐状态
     * POST /Goods/toggleRecommend?id=1&isRecommend=1
     */
    @PostMapping("/toggleRecommend")
    public R toggleRecommend(
        @RequestParam("id") Integer id,
        @RequestParam("isRecommend") Integer isRecommend) {
        goodsService.toggleRecommend(id, isRecommend);
        return R.ok();
    }

    /*
     * 批量推荐/取消推荐
     * POST /Goods/batchRecommend  body: {ids:[1,2,3], isRecommend:1}
     */
    @PostMapping("/batchRecommend")
    public R batchRecommend(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("ids");
        Integer isRecommend = Integer.valueOf(body.get("isRecommend").toString());
        goodsService.batchRecommend(ids, isRecommend);
        return R.ok();
    }

    /*
     * SKU列表（分页+筛选+价格区间）
     * GET /Goods/skuList?page=1&limit=10&categoryId=&brandId=&minPrice=&maxPrice=&keyword=
     */
    @GetMapping("/skuList")
    public R skuList(
        @RequestParam(value="page", defaultValue="1") Integer page,
        @RequestParam(value="limit", defaultValue="10") Integer limit,
        @RequestParam(value="categoryId", required=false) Integer categoryId,
        @RequestParam(value="brandId", required=false) Integer brandId,
        @RequestParam(value="minPrice", required=false) BigDecimal minPrice,
        @RequestParam(value="maxPrice", required=false) BigDecimal maxPrice,
        @RequestParam(value="keyword", required=false) String keyword) {

        IPage<SkuInfo> pageResult = goodsService.pageSku(page, limit, categoryId, brandId, minPrice, maxPrice, keyword);
        return R.ok()
            .put("data", pageResult.getRecords())
            .put("total", pageResult.getTotal());
    }

    /*
     * 更新商品基本信息（上架状态锁定）
     * PUT /Goods/update
     */
    @PutMapping("/update")
    public R update(@RequestBody GoodsInfo goods) {
        goodsService.updateGoods(goods);
        return R.ok();
    }
}
