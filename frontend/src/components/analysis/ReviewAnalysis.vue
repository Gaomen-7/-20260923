<template>
  <div class="analysis-page">
    <!-- 日期筛选 -->
    <el-row class="filter-row">
      <el-col :span="24">
        <el-date-picker v-model="dateRange" type="daterange"
          value-format="yyyy-MM-dd" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期"
          @change="loadAll">
        </el-date-picker>
        <el-button type="primary" size="small" @click="loadAll" style="margin-left:10px;">查询</el-button>
      </el-col>
    </el-row>

    <!-- 指标卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="8" v-for="card in statCards" :key="card.label">
        <div class="stat-card" :style="{borderTopColor: card.color}">
          <div class="stat-value" :style="{color: card.color}">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 评价等级饼图 + 商品口碑TOP柱状图 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>评价等级分布</span></div>
          <div id="levelPie" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>商品口碑TOP（好评率）</span></div>
          <div id="wordOfMouthBar" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 关键词词云（el-tag 模拟） -->
    <el-row class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>评价关键词（字号=提及次数）</span></div>
          <el-row :gutter="24">
            <el-col :span="12">
              <div class="cloud-box">
                <div class="cloud-title good">好评关键词</div>
                <div class="cloud-tags">
                  <el-tag v-for="k in goodKeywords" :key="'g'+k.name" type="success"
                    :style="{fontSize: k.size + 'px', margin: '4px'}">{{ k.name }}({{ k.count }})</el-tag>
                  <span v-if="goodKeywords.length === 0" class="empty-tip">暂无数据</span>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="cloud-box">
                <div class="cloud-title bad">差评关键词</div>
                <div class="cloud-tags">
                  <el-tag v-for="k in badKeywords" :key="'b'+k.name" type="danger"
                    :style="{fontSize: k.size + 'px', margin: '4px'}">{{ k.name }}({{ k.count }})</el-tag>
                  <span v-if="badKeywords.length === 0" class="empty-tip">暂无数据</span>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 口碑趋势折线图 -->
    <el-row class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>好评率趋势</span></div>
          <div id="reviewTrend" class="chart-box-lg"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 商品口碑榜表格（Tab切换） -->
    <el-row class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>商品口碑榜</span></div>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="好评TOP" name="good">
              <el-table :data="topRows" size="small" border>
                <el-table-column prop="rank" label="排名" width="60"></el-table-column>
                <el-table-column prop="product" label="商品"></el-table-column>
                <el-table-column prop="totalReviews" label="评价数"></el-table-column>
                <el-table-column prop="goodRate" label="好评率"></el-table-column>
                <el-table-column prop="avgRating" label="平均分"></el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="差评榜" name="bad">
              <el-table :data="bottomRows" size="small" border>
                <el-table-column prop="rank" label="排名" width="60"></el-table-column>
                <el-table-column prop="product" label="商品"></el-table-column>
                <el-table-column prop="totalReviews" label="评价数"></el-table-column>
                <el-table-column prop="goodRate" label="好评率"></el-table-column>
                <el-table-column prop="avgRating" label="平均分"></el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getOverview, getLevelDist, getKeywords, getCategoryCompare, getTrend, getTopProducts } from '@/api/analysis_review.js'

export default {
  name: 'ReviewAnalysis',
  data() {
    return {
      dateRange: [],
      statCards: [],
      goodKeywords: [],
      badKeywords: [],
      topRows: [],
      bottomRows: [],
      activeTab: 'good',
      levelPie: null,
      wordOfMouthBar: null,
      reviewTrend: null
    }
  },
  methods: {
    getParams() {
      var p = {}
      if (this.dateRange && this.dateRange.length === 2) {
        p.startDate = this.dateRange[0]
        p.endDate = this.dateRange[1]
      }
      return p
    },
    loadAll() {
      this.loadStatCards()
      this.loadLevelDist()
      this.loadKeywords()
      this.loadCategoryCompare()
      this.loadTrend()
      this.loadTopProducts()
    },
    loadStatCards() {
      var self = this
      getOverview(self.getParams()).then(function (resp) {
        var data = resp.data || []
        if (data.length === 0) { self.statCards = []; return }
        var total = 0, good = 0, bad = 0
        data.forEach(function (item) {
          total += Number(item.totalReviews) || 0
          good += Number(item.goodReviews) || 0
          bad += Number(item.badReviews) || 0
        })
        var goodRate = total > 0 ? (good / total) : 0
        var badRate = total > 0 ? (bad / total) : 0
        self.statCards = [
          { label: '总评价数', value: total, color: '#409EFF' },
          { label: '好评率', value: (goodRate * 100).toFixed(2) + '%', color: '#67C23A' },
          { label: '差评率', value: (badRate * 100).toFixed(2) + '%', color: '#F56C6C' }
        ]
      })
    },
    loadLevelDist() {
      var self = this
      getLevelDist(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var pieData = []
        data.forEach(function (item) {
          pieData.push({ name: item.dimensionValue, value: Number(item.totalReviews) || 0 })
        })
        self.levelPie.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
          legend: { bottom: 0 },
          series: [{ name: '评价等级', type: 'pie', radius: ['35%', '65%'], data: pieData }]
        })
      })
    },
    loadKeywords() {
      var self = this
      getKeywords(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var good = []
        var bad = []
        var maxCount = 1
        data.forEach(function (item) {
          var c = Number(item.keywordCount) || 0
          if (c > maxCount) maxCount = c
        })
        data.forEach(function (item) {
          var c = Number(item.keywordCount) || 0
          // 字号映射：12px-24px
          var size = 12 + Math.round((c / maxCount) * 12)
          var tag = { name: item.dimensionValue, count: c, size: size }
          if (item.dimensionType === 'keyword_good') {
            good.push(tag)
          } else if (item.dimensionType === 'keyword_bad') {
            bad.push(tag)
          }
        })
        self.goodKeywords = good
        self.badKeywords = bad
      })
    },
    loadCategoryCompare() {
      var self = this
      getCategoryCompare(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var names = []
        var rates = []
        data.forEach(function (item) {
          names.push(item.dimensionValue)
          rates.push(Number(item.goodRate) || 0)
        })
        self.wordOfMouthBar.setOption({
          tooltip: { trigger: 'axis' },
          grid: { left: 60, right: 20, bottom: 60 },
          xAxis: { type: 'category', data: names, axisLabel: { rotate: 30 } },
          yAxis: { type: 'value', max: 1, axisLabel: { formatter: function (v) { return (v * 100) + '%' } } },
          series: [{ name: '好评率', type: 'bar', barMaxWidth: 30, data: rates, itemStyle: { color: '#67C23A' } }]
        })
      })
    },
    loadTrend() {
      var self = this
      getTrend(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var dates = []
        var rates = []
        data.forEach(function (item) {
          dates.push(item.statDate)
          rates.push(Number(item.goodRate) || 0)
        })
        self.reviewTrend.setOption({
          tooltip: { trigger: 'axis' },
          grid: { left: 60, right: 30, bottom: 50 },
          xAxis: { type: 'category', data: dates },
          yAxis: { type: 'value', max: 1, axisLabel: { formatter: function (v) { return (v * 100) + '%' } } },
          series: [{ name: '好评率', type: 'line', smooth: true, data: rates, itemStyle: { color: '#67C23A' },
            areaStyle: { opacity: 0.2 } }]
        })
      })
    },
    loadTopProducts() {
      var self = this
      getTopProducts({ limit: 10 }).then(function (resp) {
        var data = resp.data || []
        var top = []
        var bottom = []
        data.forEach(function (item, i) {
          var row = { rank: i + 1, product: item.dimensionValue, totalReviews: item.totalReviews,
            goodRate: (Number(item.goodRate) * 100 || 0).toFixed(2) + '%', avgRating: item.avgRating }
          top.push(row)
        })
        // product_bottom 单独查询（同一接口按好评率升序截取不到，直接按 dimensionType 区分原始数据）
        self.topRows = top
        self.bottomRows = []
        // 差评榜：再次请求全量 product 数据，按好评率升序取前10
        getTopProducts({ limit: 1000 }).then(function (resp2) {
          var all = resp2.data || []
          var sorted = all.slice().sort(function (a, b) { return (Number(a.goodRate) || 0) - (Number(b.goodRate) || 0) })
          var rows = []
          sorted.slice(0, 10).forEach(function (item, i) {
            rows.push({ rank: i + 1, product: item.dimensionValue, totalReviews: item.totalReviews,
              goodRate: (Number(item.goodRate) * 100 || 0).toFixed(2) + '%', avgRating: item.avgRating })
          })
          self.bottomRows = rows
        })
      })
    },
    initCharts() {
      this.levelPie = echarts.init(document.getElementById('levelPie'))
      this.wordOfMouthBar = echarts.init(document.getElementById('wordOfMouthBar'))
      this.reviewTrend = echarts.init(document.getElementById('reviewTrend'))
    },
    resizeCharts() {
      if (this.levelPie) this.levelPie.resize()
      if (this.wordOfMouthBar) this.wordOfMouthBar.resize()
      if (this.reviewTrend) this.reviewTrend.resize()
    }
  },
  mounted() {
    this.$nextTick(function () {
      this.initCharts()
      this.loadAll()
    }.bind(this))
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    if (this.levelPie) this.levelPie.dispose()
    if (this.wordOfMouthBar) this.wordOfMouthBar.dispose()
    if (this.reviewTrend) this.reviewTrend.dispose()
  }
}
</script>

<style scoped>
.analysis-page { padding: 16px; }
.filter-row { margin-bottom: 16px; }
.stat-row { margin-bottom: 16px; }
.stat-card { background: #fff; padding: 16px; border-top: 3px solid; border-radius: 4px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.chart-row { margin-bottom: 16px; }
.chart-header { font-size: 14px; font-weight: bold; }
.chart-box { width: 100%; height: 300px; }
.chart-box-lg { width: 100%; height: 350px; }
.cloud-box { padding: 8px; }
.cloud-title { font-size: 14px; font-weight: bold; margin-bottom: 8px; }
.cloud-title.good { color: #67C23A; }
.cloud-title.bad { color: #F56C6C; }
.cloud-tags { display: flex; flex-wrap: wrap; align-items: center; min-height: 60px; }
.empty-tip { color: #C0C4CC; font-size: 12px; }
</style>
