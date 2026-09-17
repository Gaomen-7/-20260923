package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.MemberLevelRuleMapper;
import com.gec.dao.MemberMapper;
import com.gec.domain.entity.Member;
import com.gec.domain.entity.MemberLevelRule;
import com.gec.domain.entity.OrderInfo;
import com.gec.domain.search.MemberSearch;
import com.gec.service.IMemberService;
import com.gec.service.IOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl
    extends ServiceImpl<MemberMapper, Member>
    implements IMemberService {

    @Autowired
    private MemberLevelRuleMapper levelRuleMapper;

    @Autowired
    @Lazy
    private IOrderInfoService orderInfoService;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public IPage<Member> pageMember(Page page, MemberSearch search) {
        QueryWrapper<Member> qw = new QueryWrapper<>();
        if (search.getMemberType() != null) {
            qw.eq("member_type", search.getMemberType());
        }
        if (search.getStatus() != null) {
            qw.eq("status", search.getStatus());
        }
        if (search.getSource() != null && !search.getSource().isEmpty()) {
            qw.eq("source", search.getSource());
        }
        if (search.getPhone() != null && !search.getPhone().trim().isEmpty()) {
            qw.like("phone", search.getPhone().trim());
        }
        qw.orderByDesc("id");
        return baseMapper.selectPage(page, qw);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("total", this.count());
        result.put("blacklist", this.count(new QueryWrapper<Member>().eq("status", 0)));
        result.put("normal", this.count(new QueryWrapper<Member>().eq("member_type", 1)));
        result.put("vip", this.count(new QueryWrapper<Member>().eq("member_type", 2)));
        result.put("gold", this.count(new QueryWrapper<Member>().eq("member_type", 3)));
        return result;
    }

    @Override
    @Transactional
    public void updateMember(Member member) {
        if (member.getId() == null) {
            throw new RuntimeException("会员ID不能为空");
        }
        Member exist = this.getById(member.getId());
        if (exist == null) {
            throw new RuntimeException("会员不存在");
        }
        member.setUpdateTime(now());
        boolean ret = this.updateById(member);
        if (!ret) {
            throw new RuntimeException("更新会员失败");
        }
    }

    @Override
    @Transactional
    public void batchDisable(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要禁用的会员");
        }
        for (Integer id : ids) {
            Member m = new Member();
            m.setId(id);
            m.setStatus(0);
            m.setUpdateTime(now());
            this.updateById(m);
        }
    }

    @Override
    public List<OrderInfo> getConsumeRecord(Integer memberId) {
        QueryWrapper<OrderInfo> qw = new QueryWrapper<>();
        qw.eq("user_id", memberId);
        qw.orderByDesc("id");
        return orderInfoService.list(qw);
    }

    @Override
    public void exportMembers(MemberSearch search, HttpServletResponse response) {
        List<Member> list = this.pageMember(new Page<>(1, Integer.MAX_VALUE), search).getRecords();
        response.setContentType("text/csv;charset=UTF-8");
        try {
            String fileName = URLEncoder.encode("会员列表_" + now().replaceAll("[-: ]", "") + ".csv", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            OutputStream out = response.getOutputStream();
            out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            StringBuilder sb = new StringBuilder();
            sb.append("会员ID,昵称,手机号,会员类型,来源,余额,积分,状态,注册时间\n");
            for (Member m : list) {
                sb.append(m.getId()).append(",");
                sb.append(escapeCsv(m.getNickname())).append(",");
                sb.append(escapeCsv(m.getPhone())).append(",");
                sb.append(getMemberTypeLabel(m.getMemberType())).append(",");
                sb.append(escapeCsv(m.getSource())).append(",");
                sb.append(m.getBalance() != null ? m.getBalance() : "").append(",");
                sb.append(m.getPoints() != null ? m.getPoints() : 0).append(",");
                sb.append(m.getStatus() != null && m.getStatus() == 1 ? "正常" : "黑名单").append(",");
                sb.append(escapeCsv(m.getRegisterTime())).append("\n");
            }
            out.write(sb.toString().getBytes("UTF-8"));
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException("导出失败：" + e.getMessage());
        }
    }

    @Override
    public List<MemberLevelRule> listLevelRules() {
        return levelRuleMapper.selectList(null);
    }

    @Override
    @Transactional
    public void saveLevelRule(MemberLevelRule rule) {
        if (rule.getLevelName() == null || rule.getLevelName().trim().isEmpty()) {
            throw new RuntimeException("等级名称不能为空");
        }
        if (rule.getMinPoints() == null) rule.setMinPoints(0);
        if (rule.getDiscountRate() == null) rule.setDiscountRate(BigDecimal.ONE);
        if (rule.getId() == null) {
            rule.setCreateTime(now());
            levelRuleMapper.insert(rule);
        } else {
            rule.setUpdateTime(now());
            levelRuleMapper.updateById(rule);
        }
    }

    @Override
    @Transactional
    public void deleteLevelRule(Integer id) {
        int ret = levelRuleMapper.deleteById(id);
        if (ret <= 0) throw new RuntimeException("删除等级规则失败");
    }

    private String getMemberTypeLabel(Integer type) {
        if (type == null) return "未知";
        if (type == 1) return "普通会员";
        if (type == 2) return "VIP会员";
        if (type == 3) return "黄金会员";
        return "未知";
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
