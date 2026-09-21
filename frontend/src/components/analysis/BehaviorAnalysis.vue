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

    <!-- 图表行1：流量入口饼图 + 入口转化率柱状图 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>流量入口分布（PV）</span></div>
          <div id="sourcePie" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>各入口转化率</span></div>
          <div id="sourceBar" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表行2：行为趋势折线图 -->
    <el-row class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>用户行为趋势（按行为类型）</span></div>
          <div id="trendLine" class="chart-box-lg"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表行3：转化漏斗 + 时段热力图 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>转化漏斗</span></div>
          <div id="funnelChart" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>时段热力图（日期 × 小时，PV）</span></div>
          <div id="heatmapChart" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表格行：热搜关键词 + 热门商品 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>热搜关键词榜 TOP10</span></div>
          <el-table :data="keywordRows" size="small" border>
            <el-table-column prop="rank" label="排名" width="60"></el-table-column>
            <el-table-column prop="keyword" label="关键词"></el-table-column>
            <el-table-column prop="pv" label="搜索PV"></el-table-column>
            <el-table-column prop="uv" label="搜索UV"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>热门商品榜 TOP10</span></div>
          <el-table :data="productRows" size="small" border>
            <el-table-column prop="rank" label="排名" width="60"></el-table-column>
            <el-table-column prop="product" label="商品"></el-table-column>
            <el-table-column prop="pv" label="浏览PV"></el-table-column>
            <el-table-column prop="uv" label="浏览UV"></el-table-column>
            <el-table-column prop="conversionCount" label="转化数"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getSource, getTrend, getKeywords, getFunnel, getHeatmap, getTopProducts } from '@/api/analysis_behavior.js'

export default {
  name: 'BehaviorAnalysis',
  data() {
    return {
      dateRange: [],
      keywordRows: [],
      productRows: [],
      sourcePie: null,
      sourceBar: null,
      trendLine: null,
      funnelChart: null,
      heatmapChart: null
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
      this.loadSource()
      this.loadTrend()
      this.loadFunnel()
      this.loadHeatmap()
      this.loadKeywords()
      this.loadTopProducts()
    },
    loadSource() {
      var self = this
      getSource(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var pieData = []
        var barNames = []
        var barRates = []
        data.forEach(function (item) {
          pieData.push({ name: item.dimensionValue, value: item.pv || 0 })
          barNames.push(item.dimensionValue)
          barRates.push(Number(item.conversionRate) || 0)
        })
        self.sourcePie.setOption({
          tooltip: { trigger: 'item' },
          legend: { type: 'scroll', bottom: 0 },
          series: [{
            name: '流量入口PV', type: 'pie', radius: ['35%', '65%'],
            label: { formatter: '{b}: {c}' },
            data: pieData
          }]
        })
        self.sourceBar.setOption({
          tooltip: { trigger: 'axis' },
          grid: { left: 60, right: 20, bottom: 40 },
          xAxis: { type: 'category', data: barNames, axisLabel: { rotate: 30 } },
          yAxis: { type: 'value' },
          series: [{ name: '转化率', type: 'bar', barMaxWidth: 40, data: barRates,
            itemStyle: { color: '#409EFF' } }]
        })
      })
    },
    loadTrend() {
      var self = this
      getTrend(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var dates = []
        var dateMap = {}
        var seriesMap = {}
        data.forEach(function (item) {
          if (!dateMap[item.statDate]) {
            dateMap[item.statDate] = dates.length
            dates.push(item.statDate)
          }
        })
        data.forEach(function (item) {
          var sName = item.dimensionValue
          if (!seriesMap[sName]) seriesMap[sName] = new Array(dates.length).fill(0)
          seriesMap[sName][dateMap[item.statDate]] = Number(item.pv) || 0
        })
        var series = []
        for (var name in seriesMap) {
          series.push({ name: name, type: 'line', smooth: true, data: seriesMap[name] })
        }
        self.trendLine.setOption({
          tooltip: { trigger: 'axis' },
          legend: { type: 'scroll', bottom: 0 },
          grid: { left: 60, right: 30, bottom: 60 },
          xAxis: { type: 'category', data: dates },
          yAxis: { type: 'value' },
          series: series
        })
      })
    },
    loadFunnel() {
      var self = this
      // funnel 维度的环节去重人数放在 pv 字段（conversionCount 语义为转化成功人数，funnel 下恒为 NULL）
      var STEPS = ['浏览', '搜索', '加购', '下单', '支付']
      getFunnel(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var sumMap = {}
        data.forEach(function (item) {
          var name = item.dimensionValue
          sumMap[name] = (sumMap[name] || 0) + (Number(item.pv) || 0)
        })
        var funnelData = []
        STEPS.forEach(function (step) {
          funnelData.push({ name: step, value: sumMap[step] || 0 })
        })
        self.funnelChart.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: {c}人' },
          series: [{ name: '转化漏斗', type: 'funnel', sort: 'none',
            label: { formatter: '{b}: {c}' }, data: funnelData }]
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
          var v = Number(item.pv) || 0
          if (v > maxVal) maxVal = v
          heatData.push([h, d, v])
        })
        self.heatmapChart.setOption({
          tooltip: { formatter: function (p) { return p.value[2] } },
          grid: { left: 90, right: 60, bottom: 40 },
          xAxis: { type: 'category', data: hours, splitArea: { show: true } },
          yAxis: { type: 'category', data: days, splitArea: { show: true } },
          visualMap: { min: 0, max: maxVal || 1, calculable: true, orient: 'vertical', right: 5, top: 'center' },
          series: [{ name: 'PV', type: 'heatmap', data: heatData }]
        })
      })
    },
    loadKeywords() {
      var self = this
      getKeywords({ limit: 10 }).then(function (resp) {
        var data = resp.data || []
        var rows = []
        data.forEach(function (item, i) {
          rows.push({ rank: i + 1, keyword: item.dimensionValue, pv: item.pv, uv: item.uv })
        })
        self.keywordRows = rows
      })
    },
    loadTopProducts() {
      var self = this
      getTopProducts({ limit: 10 }).then(function (resp) {
        var data = resp.data || []
        var rows = []
        data.forEach(function (item, i) {
          rows.push({ rank: i + 1, product: item.dimensionValue, pv: item.pv, uv: item.uv, conversionCount: item.conversionCount })
        })
        self.productRows = rows
      })
    },
    initCharts() {
      this.sourcePie = echarts.init(document.getElementById('sourcePie'))
      this.sourceBar = echarts.init(document.getElementById('sourceBar'))
      this.trendLine = echarts.init(document.getElementById('trendLine'))
      this.funnelChart = echarts.init(document.getElementById('funnelChart'))
      this.heatmapChart = echarts.init(document.getElementById('heatmapChart'))
    },
    resizeCharts() {
      if (this.sourcePie) this.sourcePie.resize()
      if (this.sourceBar) this.sourceBar.resize()
      if (this.trendLine) this.trendLine.resize()
      if (this.funnelChart) this.funnelChart.resize()
      if (this.heatmapChart) this.heatmapChart.resize()
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
    if (this.sourcePie) this.sourcePie.dispose()
    if (this.sourceBar) this.sourceBar.dispose()
    if (this.trendLine) this.trendLine.dispose()
    if (this.funnelChart) this.funnelChart.dispose()
    if (this.heatmapChart) this.heatmapChart.dispose()
  }
}
</script>

<style scoped>
.analysis-page { padding: 16px; }
.filter-row { margin-bottom: 16px; }
.chart-row { margin-bottom: 16px; }
.chart-header { font-size: 14px; font-weight: bold; }
.chart-box { width: 100%; height: 300px; }
.chart-box-lg { width: 100%; height: 350px; }
</style>
