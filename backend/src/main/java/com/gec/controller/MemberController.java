package com.gec.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.domain.entity.Member;
import com.gec.domain.entity.MemberLevelRule;
import com.gec.domain.search.MemberSearch;
import com.gec.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Member")
public class MemberController extends BaseController {

    @Autowired
    private IMemberService memberService;

    /* 1. 分页查询会员列表 */
    @PostMapping("/list/{page}/{limit}")
    public R list(
            @PathVariable("page") Integer page,
            @PathVariable("limit") Integer limit,
            @RequestBody MemberSearch param) {
        Page frmPage = newPage(page, limit);
        IPage<Member> retPage = memberService.pageMember(frmPage, param);
        return R.convertPage(retPage);
    }

    /* 2. 会员统计 */
    @GetMapping("/statistics")
    public R statistics() {
        return R.ok().put("data", memberService.getStatistics());
    }

    /* 3. 会员详情 */
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable("id") Integer id) {
        Member member = memberService.getById(id);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        return R.ok().put("data", member);
    }

    /* 4. 更新会员 */
    @PutMapping("/update")
    public R update(@RequestBody Member member) {
        memberService.updateMember(member);
        return R.ok();
    }

    /* 5. 批量禁用（设为黑名单） */
    @PostMapping("/batchDisable")
    public R batchDisable(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("ids");
        memberService.batchDisable(ids);
        return R.ok();
    }

    /* 6. 会员消费记录 */
    @GetMapping("/consumeRecord/{memberId}")
    public R consumeRecord(@PathVariable("memberId") Integer memberId) {
        return R.ok().put("data", memberService.getConsumeRecord(memberId));
    }

    /* 7. 导出会员CSV */
    @PostMapping("/export")
    public void export(@RequestBody MemberSearch param, HttpServletResponse response) {
        memberService.exportMembers(param, response);
    }

    /* 8. 等级规则列表 */
    @GetMapping("/levelRules")
    public R levelRules() {
        return R.ok().put("data", memberService.listLevelRules());
    }

    /* 9. 保存等级规则（新增/更新） */
    @PostMapping("/levelRule/save")
    public R saveLevelRule(@RequestBody MemberLevelRule rule) {
        memberService.saveLevelRule(rule);
        return R.ok();
    }

    /* 10. 删除等级规则 */
    @DeleteMapping("/levelRule/{id}")
    public R deleteLevelRule(@PathVariable("id") Integer id) {
        memberService.deleteLevelRule(id);
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
