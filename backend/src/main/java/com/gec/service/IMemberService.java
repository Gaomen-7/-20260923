package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.Member;
import com.gec.domain.entity.MemberLevelRule;
import com.gec.domain.entity.OrderInfo;
import com.gec.domain.search.MemberSearch;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface IMemberService extends IService<Member> {

    /* 分页查询会员列表 */
    IPage<Member> pageMember(Page page, MemberSearch search);

    /* 统计：总数、黑名单数、普通会员数、VIP数、黄金会员数 */
    Map<String, Object> getStatistics();

    /* 更新会员信息 */
    void updateMember(Member member);

    /* 批量禁用（设为黑名单） */
    void batchDisable(List<Integer> ids);

    /* 查询会员消费记录（订单列表） */
    List<OrderInfo> getConsumeRecord(Integer memberId);

    /* 导出会员CSV */
    void exportMembers(MemberSearch search, HttpServletResponse response);

    /* 等级规则列表 */
    List<MemberLevelRule> listLevelRules();

    /* 保存等级规则（新增或更新） */
    void saveLevelRule(MemberLevelRule rule);

    /* 删除等级规则 */
    void deleteLevelRule(Integer id);
}
