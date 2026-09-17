package com.gec.controller;

import com.gec.components.FileTemplate;
import com.gec.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/Dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private IDashboardService dashboardService;

    /* 1. 顶部统计概览 */
    @GetMapping("/statistics")
    public R statistics() {
        return R.ok().put("data", dashboardService.getStatistics());
    }

    /* 2. 订单状态分布（饼图数据） */
    @GetMapping("/orderStatus")
    public R orderStatus() {
        return R.ok().put("data", dashboardService.getOrderStatus());
    }

    /* 3. 最近订单（5条） */
    @GetMapping("/recentOrders")
    public R recentOrders() {
        return R.ok().put("data", dashboardService.getRecentOrders());
    }

    /* 4. 商品上下架分布（饼图） */
    @GetMapping("/goodsStatus")
    public R goodsStatus() {
        return R.ok().put("data", dashboardService.getGoodsStatus());
    }

    /* 5. 会员类型分布（饼图） */
    @GetMapping("/memberType")
    public R memberType() {
        return R.ok().put("data", dashboardService.getMemberType());
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
