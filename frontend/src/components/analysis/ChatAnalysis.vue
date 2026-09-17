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

    <!-- 咨询类型饼图 + 咨询转化柱状图 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>咨询类型分布（会话数）</span></div>
          <div id="typePie" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>各类型咨询转化率</span></div>
          <div id="conversionBar" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 时段热力图 -->
    <el-row class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>咨询时段热力图（日期 × 小时，会话数）</span></div>
          <div id="chatHeatmap" class="chart-box-lg"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门咨询商品表格 -->
    <el-row class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>热门咨询商品 TOP10</span></div>
          <el-table :data="productRows" size="small" border>
            <el-table-column prop="rank" label="排名" width="60"></el-table-column>
            <el-table-column prop="product" label="商品"></el-table-column>
            <el-table-column prop="sessionCount" label="咨询次数"></el-table-column>
            <el-table-column prop="userCount" label="咨询用户数"></el-table-column>
            <el-table-column prop="conversionRate" label="转化率"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getOverview, getTypeDist, getHeatmap, getConversion, getTopProducts } from '@/api/analysis_chat.js'

export default {
  name: 'ChatAnalysis',
  data() {
    return {
      dateRange: [],
      statCards: [],
      productRows: [],
      typePie: null,
      conversionBar: null,
      chatHeatmap: null
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
      this.loadTypeDist()
      this.loadConversion()
      this.loadHeatmap()
      this.loadTopProducts()
    },
    loadStatCards() {
      var self = this
      getOverview(self.getParams()).then(function (resp) {
        var data = resp.data || []
        if (data.length === 0) { self.statCards = []; return }
        var sessions = 0, resolved = 0, converted = 0, firstResp = 0
        data.forEach(function (item) {
          sessions += Number(item.sessionCount) || 0
          resolved += Number(item.resolutionRate) || 0
          converted += Number(item.conversionRate) || 0
          firstResp += Number(item.avgFirstResponse) || 0
        })
        var n = data.length
        self.statCards = [
          { label: '有效会话数', value: sessions, color: '#409EFF' },
          { label: '平均响应时长', value: (firstResp / n).toFixed(1) + '秒', color: '#E6A23C' },
          { label: '问题解决率', value: ((resolved / n) * 100).toFixed(2) + '%', color: '#67C23A' }
        ]
      })
    },
    loadTypeDist() {
      var self = this
      getTypeDist(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var pieData = []
        data.forEach(function (item) {
          pieData.push({ name: item.dimensionValue, value: Number(item.sessionCount) || 0 })
        })
        self.typePie.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
          legend: { type: 'scroll', bottom: 0 },
          series: [{ name: '咨询类型', type: 'pie', radius: ['35%', '65%'], data: pieData }]
        })
      })
    },
    loadConversion() {
      var self = this
      getConversion(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var names = []
        var rates = []
        var counts = []
        data.forEach(function (item) {
          names.push(item.dimensionValue)
          rates.push(Number(item.conversionRate) || 0)
          counts.push(Number(item.conversionCount) || 0)
        })
        self.conversionBar.setOption({
          tooltip: { trigger: 'axis' },
          legend: { data: ['转化率', '转化数'], bottom: 0 },
          grid: { left: 60, right: 60, bottom: 60 },
          xAxis: { type: 'category', data: names, axisLabel: { rotate: 30 } },
          yAxis: [
            { type: 'value', max: 1, axisLabel: { formatter: function (v) { return (v * 100) + '%' } } },
            { type: 'value', name: '转化数' }
          ],
          series: [
            { name: '转化率', type: 'bar', barMaxWidth: 30, data: rates, itemStyle: { color: '#409EFF' } },
            { name: '转化数', type: 'bar', yAxisIndex: 1, barMaxWidth: 30, data: counts, itemStyle: { color: '#E6A23C' } }
          ]
        })
      })
    },
    loadHeatmap() {
      var self = this
      getHeatmap(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var hours = []
        for (var i = 0; i < 24; i++) hours.push(i + '时')
        var days = []
        var dayMap = {}
        data.forEach(function (item) {
          if (!dayMap[item.statDate]) {
            dayMap[item.statDate] = days.length
            days.push(item.statDate)
          }
        })
        var heatData = []
        var maxVal = 0
        data.forEach(function (item) {
          var h = parseInt(item.dimensionValue)
          var d = dayMap[item.statDate]
          var v = Number(item.sessionCount) || 0
          if (v > maxVal) maxVal = v
          heatData.push([h, d, v])
        })
        self.chatHeatmap.setOption({
          tooltip: { formatter: function (p) { return p.value[2] } },
          grid: { left: 90, right: 60, bottom: 40 },
          xAxis: { type: 'category', data: hours, splitArea: { show: true } },
          yAxis: { type: 'category', data: days, splitArea: { show: true } },
          visualMap: { min: 0, max: maxVal || 1, calculable: true, orient: 'vertical', right: 5, top: 'center' },
          series: [{ name: '会话数', type: 'heatmap', data: heatData }]
        })
      })
    },
    loadTopProducts() {
      var self = this
      getTopProducts({ limit: 10 }).then(function (resp) {
        var data = resp.data || []
        var rows = []
        data.forEach(function (item, i) {
          rows.push({ rank: i + 1, product: item.dimensionValue, sessionCount: item.sessionCount,
            userCount: item.userCount, conversionRate: (Number(item.conversionRate) * 100 || 0).toFixed(2) + '%' })
        })
        self.productRows = rows
      })
    },
    initCharts() {
      this.typePie = echarts.init(document.getElementById('typePie'))
      this.conversionBar = echarts.init(document.getElementById('conversionBar'))
      this.chatHeatmap = echarts.init(document.getElementById('chatHeatmap'))
    },
    resizeCharts() {
      if (this.typePie) this.typePie.resize()
      if (this.conversionBar) this.conversionBar.resize()
      if (this.chatHeatmap) this.chatHeatmap.resize()
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
    if (this.typePie) this.typePie.dispose()
    if (this.conversionBar) this.conversionBar.dispose()
    if (this.chatHeatmap) this.chatHeatmap.dispose()
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
</style>
