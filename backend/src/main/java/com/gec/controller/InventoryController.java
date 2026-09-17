package com.gec.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gec.components.FileTemplate;
import com.gec.domain.entity.InventoryLog;
import com.gec.service.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/Inventory")
public class InventoryController extends BaseController {

    @Autowired
    private IInventoryService inventoryService;

    /* 1. 库存统计：商品总数、库存总量、低库存数、库存总价值 */
    @GetMapping("/statistics")
    public R statistics() {
        return R.ok().put("data", inventoryService.getStatistics());
    }

    /* 2. 库存列表（分页，基于商品，聚合SKU数量） */
    @PostMapping("/list/{page}/{limit}")
    public R list(
            @PathVariable("page") Integer page,
            @PathVariable("limit") Integer limit,
            @RequestBody Map<String, Object> param) {
        return R.ok().put("data", inventoryService.pageInventory(page, limit, param));
    }

    /* 3. 某商品的SKU库存明细 */
    @GetMapping("/skuDetail/{goodsId}")
    public R skuDetail(@PathVariable("goodsId") Integer goodsId) {
        return R.ok().put("data", inventoryService.getSkuDetail(goodsId));
    }

    /* 4. 调整商品库存 */
    @PostMapping("/adjust")
    public R adjustStock(@RequestBody Map<String, Object> body) {
        Integer goodsId = body.get("goodsId") != null ? Integer.valueOf(body.get("goodsId").toString()) : null;
        Integer stock = body.get("stock") != null ? Integer.valueOf(body.get("stock").toString()) : null;
        inventoryService.adjustStock(goodsId, stock);
        return R.ok();
    }

    /* 5. 调整SKU库存 */
    @PostMapping("/adjustSku")
    public R adjustSkuStock(@RequestBody Map<String, Object> body) {
        Integer skuId = body.get("skuId") != null ? Integer.valueOf(body.get("skuId").toString()) : null;
        Integer stock = body.get("stock") != null ? Integer.valueOf(body.get("stock").toString()) : null;
        inventoryService.adjustSkuStock(skuId, stock);
        return R.ok();
    }

    /* 6. 库存流水分页查询 */
    @GetMapping("/log/{page}/{limit}")
    public R inventoryLog(
            @PathVariable("page") Integer page,
            @PathVariable("limit") Integer limit,
            @RequestParam(required = false) Integer goodsId,
            @RequestParam(required = false) Integer skuId,
            @RequestParam(required = false) Integer changeType) {
        IPage<InventoryLog> result = inventoryService.pageInventoryLog(page, limit, goodsId, skuId, changeType);
        return R.convertPage(result);
    }

    /* 7. 库存盘点 */
    @PostMapping("/stocktake")
    public R stocktake(@RequestBody Map<String, Object> body) {
        Integer goodsId = body.get("goodsId") != null ? Integer.valueOf(body.get("goodsId").toString()) : null;
        Integer skuId = body.get("skuId") != null ? Integer.valueOf(body.get("skuId").toString()) : null;
        Integer actualStock = body.get("actualStock") != null ? Integer.valueOf(body.get("actualStock").toString()) : null;
        String remark = body.get("remark") != null ? body.get("remark").toString() : null;
        inventoryService.stocktake(goodsId, skuId, actualStock, "admin", remark);
        return R.ok();
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
