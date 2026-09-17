package com.gec.controller.analysis;

import com.gec.controller.BaseController;
import com.gec.controller.R;
import com.gec.dao.UserBehaviorStatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 用户行为分析接口（数据源：ads_user_behavior）
 */
@RestController
@RequestMapping("/analysis/behavior")
public class BehaviorAnalysisController extends BaseController {

    @Autowired
    private UserBehaviorStatMapper userBehaviorStatMapper;

    /* 1. 流量入口分布（饼图+柱状图） */
    @GetMapping("/source")
    public R source(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", userBehaviorStatMapper.selectByDimensionType("source", startDate, endDate));
    }

    /* 2. 行为趋势（折线图，按日期+行为类型） */
    @GetMapping("/trend")
    public R trend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", userBehaviorStatMapper.selectByDimensionType("behavior", startDate, endDate));
    }

    /* 3. 热搜关键词榜 */
    @GetMapping("/keywords")
    public R keywords(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data",
                userBehaviorStatMapper.selectTopByDimensionType("keyword", "pv", limit, startDate, endDate));
    }

    /* 4. 转化漏斗 */
    @GetMapping("/funnel")
    public R funnel(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", userBehaviorStatMapper.selectByDimensionType("funnel", startDate, endDate));
    }

    /* 5. 时段热力图 */
    @GetMapping("/heatmap")
    public R heatmap(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", userBehaviorStatMapper.selectByDimensionType("hour", startDate, endDate));
    }

    /* 6. 热门商品榜（浏览+加购+收藏合并，按pv排序） */
    @GetMapping("/topProducts")
    public R topProducts(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", userBehaviorStatMapper.selectTopProducts(limit, startDate, endDate));
    }

    @ExceptionHandler(Exception.class)
    public void exceptionFallback(Exception e, HttpServletResponse resp) {
        e.printStackTrace();
        R.err(e).write(resp);
    }

    @Override
    protected com.gec.components.FileTemplate getFileTemplate() { return null; }
}
