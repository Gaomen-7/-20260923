package com.gec.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.domain.bo.OrderInfoBO;
import com.gec.domain.search.OrderInfoSearch;
import com.gec.service.IOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Order")
public class OrderController extends BaseController {

    @Autowired
    private IOrderInfoService orderInfoService;

    /* 1. 分页查询订单列表 */
    @PostMapping("/list/{page}/{limit}")
    public R list(
            @PathVariable("page") Integer page,
            @PathVariable("limit") Integer limit,
            @RequestBody OrderInfoSearch param) {
        Page frmPage = newPage(page, limit);
        IPage<OrderInfoBO> retPage = orderInfoService.pageOrder(frmPage, param);
        return R.convertPage(retPage);
    }

    /* 2. 订单详情（含商品明细） */
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable("id") Integer id) {
        OrderInfoBO bo = orderInfoService.getOrderDetail(id);
        return R.ok(bo);
    }

    /* 3. 发货（待发货→已发货） */
    @PutMapping("/ship/{id}")
    public R ship(@PathVariable("id") Integer id) {
        orderInfoService.shipOrder(id);
        return R.ok();
    }

    /* 4. 修改价格（仅待付款） */
    @PutMapping("/updatePrice")
    public R updatePrice(@RequestBody Map<String, Object> body) {
        Integer id = Integer.valueOf(body.get("id").toString());
        BigDecimal actualAmount = new BigDecimal(body.get("actualAmount").toString());
        orderInfoService.updatePrice(id, actualAmount);
        return R.ok();
    }

    /* 5. 取消订单（待付款/待发货→已取消） */
    @PutMapping("/cancel/{id}")
    public R cancel(@PathVariable("id") Integer id) {
        orderInfoService.cancelOrder(id);
        return R.ok();
    }

    /* 6. 删除订单（仅已完成/已取消） */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id") Integer id) {
        orderInfoService.deleteOrder(id);
        return R.ok();
    }

    /* 7. 修改备注 */
    @PutMapping("/updateRemark")
    public R updateRemark(@RequestBody Map<String, Object> body) {
        Integer id = Integer.valueOf(body.get("id").toString());
        String remark = body.get("remark") != null ? body.get("remark").toString() : "";
        orderInfoService.updateRemark(id, remark);
        return R.ok();
    }

    /* 8. 批量删除 */
    @DeleteMapping("/batchDelete")
    public R batchDelete(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("ids");
        orderInfoService.batchDelete(ids);
        return R.ok();
    }

    /* 9. 导出订单CSV */
    @PostMapping("/export")
    public void export(@RequestBody OrderInfoSearch param, HttpServletResponse response) {
        orderInfoService.exportOrders(param, response);
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
