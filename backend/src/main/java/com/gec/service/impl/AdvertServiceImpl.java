package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.AdvertMapper;
import com.gec.domain.entity.Advert;
import com.gec.domain.search.AdvertSearch;
import com.gec.service.IAdvertService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdvertServiceImpl
    extends ServiceImpl<AdvertMapper, Advert>
    implements IAdvertService {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public IPage<Advert> pageAdvert(Page page, AdvertSearch search) {
        QueryWrapper<Advert> qw = new QueryWrapper<>();
        if (search.getAdvertName() != null && !search.getAdvertName().trim().isEmpty()) {
            qw.like("advert_name", search.getAdvertName().trim());
        }
        if (search.getPosition() != null && !search.getPosition().isEmpty()) {
            qw.eq("position", search.getPosition());
        }
        if (search.getStatus() != null) {
            qw.eq("status", search.getStatus());
        }
        qw.orderByDesc("id");
        IPage<Advert> result = baseMapper.selectPage(page, qw);
        /* 逐条计算有效状态，写入 transient 字段 */
        String today = LocalDate.now().toString();
        for (Advert a : result.getRecords()) {
            a.setEffectiveStatus(calcEffectiveStatus(a, today));
        }
        return result;
    }

    /* 计算广告有效状态：0=已下线 1=投放中 2=未开始 3=已过期 */
    private Integer calcEffectiveStatus(Advert a, String today) {
        if (a.getStatus() == null || a.getStatus() == 0) return 0;
        String start = a.getStartTime();
        String end = a.getEndTime();
        if (start != null && !start.isEmpty() && today.compareTo(start) < 0) return 2;
        if (end != null && !end.isEmpty() && today.compareTo(end) > 0) return 3;
        return 1;
    }

    @Override
    @Transactional
    public void addAdvert(Advert advert) {
        if (advert.getAdvertName() == null || advert.getAdvertName().trim().isEmpty()) {
            throw new RuntimeException("广告名称不能为空");
        }
        if (advert.getStatus() == null) {
            advert.setStatus(1);
        }
        if (advert.getWeight() == null) {
            advert.setWeight(1);
        }
        if (advert.getIsFirst() == null) {
            advert.setIsFirst(0);
        }
        if (advert.getTotalViews() == null) {
            advert.setTotalViews(0);
        }
        if (advert.getCurrentViews() == null) {
            advert.setCurrentViews(0);
        }
        advert.setCreateTime(now());
        boolean ret = this.save(advert);
        if (!ret) {
            throw new RuntimeException("添加广告失败");
        }
    }

    @Override
    @Transactional
    public void updateAdvert(Advert advert) {
        if (advert.getId() == null) {
            throw new RuntimeException("广告ID不能为空");
        }
        Advert exist = this.getById(advert.getId());
        if (exist == null) {
            throw new RuntimeException("广告不存在");
        }
        advert.setUpdateTime(now());
        boolean ret = this.updateById(advert);
        if (!ret) {
            throw new RuntimeException("更新广告失败");
        }
    }

    @Override
    @Transactional
    public void batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的广告");
        }
        boolean ret = this.removeByIds(ids);
        if (!ret) {
            throw new RuntimeException("批量删除失败");
        }
    }

    @Override
    @Transactional
    public void toggleStatus(Integer id, Integer status) {
        Advert advert = this.getById(id);
        if (advert == null) {
            throw new RuntimeException("广告不存在");
        }
        advert.setStatus(status);
        advert.setUpdateTime(now());
        boolean ret = this.updateById(advert);
        if (!ret) {
            throw new RuntimeException("状态切换失败");
        }
    }

    @Override
    @Transactional
    public int refreshExpired() {
        String today = LocalDate.now().toString();
        QueryWrapper<Advert> qw = new QueryWrapper<>();
        qw.eq("status", 1);
        qw.isNotNull("end_time");
        qw.lt("end_time", today);
        List<Advert> expiredList = this.list(qw);
        int count = 0;
        for (Advert a : expiredList) {
            Advert update = new Advert();
            update.setId(a.getId());
            update.setStatus(0);
            update.setUpdateTime(now());
            this.updateById(update);
            count++;
        }
        return count;
    }

    @Override
    @Transactional
    public void incrementView(Integer id) {
        Advert advert = this.getById(id);
        if (advert == null) throw new RuntimeException("广告不存在");
        int current = advert.getCurrentViews() != null ? advert.getCurrentViews() : 0;
        Advert update = new Advert();
        update.setId(id);
        update.setCurrentViews(current + 1);
        boolean ret = this.updateById(update);
        if (!ret) throw new RuntimeException("曝光量更新失败");
    }

    @Override
    @Transactional
    public void incrementClick(Integer id) {
        Advert advert = this.getById(id);
        if (advert == null) throw new RuntimeException("广告不存在");
        int current = advert.getClickCount() != null ? advert.getClickCount() : 0;
        Advert update = new Advert();
        update.setId(id);
        update.setClickCount(current + 1);
        boolean ret = this.updateById(update);
        if (!ret) throw new RuntimeException("点击量更新失败");
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
