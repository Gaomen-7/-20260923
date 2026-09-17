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
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <div class="stat-card" :style="{borderTopColor: card.color}">
          <div class="stat-value" :style="{color: card.color}">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 销售趋势折线图（双Y轴） -->
    <el-row class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>销售趋势（金额 + 订单数）</span></div>
          <div id="orderTrend" class="chart-box-lg"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 品类饼图 + 退换货原因饼图 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>品类销售结构（销售额）</span></div>
          <div id="categoryPie" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>退换货原因分布</span></div>
          <div id="returnPie" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 客单价直方图 + 时段销售柱状图 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>客单价区间分布</span></div>
          <div id="priceRangeBar" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header"><span>时段销售（按小时汇总）</span></div>
          <div id="hourlyBar" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getOverview, getTrend, getCategory, getReturnBoard, getPriceRange, getHourly } from '@/api/analysis_order.js'

export default {
  name: 'OrderAnalysis',
  data() {
    return {
      dateRange: [],
      statCards: [],
      orderTrend: null,
      categoryPie: null,
      returnPie: null,
      priceRangeBar: null,
      hourlyBar: null
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
      this.loadTrend()
      this.loadCategory()
      this.loadReturn()
      this.loadPriceRange()
      this.loadHourly()
    },
    loadStatCards() {
      var self = this
      getOverview(self.getParams()).then(function (resp) {
        var data = resp.data || []
        if (data.length === 0) { self.statCards = []; return }
        var agg = { totalAmount: 0, orderCount: 0, paidOrderCount: 0, cancelCount: 0, returnCount: 0, avgOrderValue: 0, payConversionRate: 0 }
        data.forEach(function (item) {
          agg.totalAmount += Number(item.totalAmount) || 0
          agg.orderCount += Number(item.orderCount) || 0
          agg.paidOrderCount += Number(item.paidOrderCount) || 0
          agg.cancelCount += Number(item.cancelCount) || 0
          agg.returnCount += Number(item.returnCount) || 0
          agg.avgOrderValue += Number(item.avgOrderValue) || 0
          agg.payConversionRate += Number(item.payConversionRate) || 0
        })
        var avgValue = agg.avgOrderValue / data.length
        var returnRate = agg.orderCount > 0 ? (agg.returnCount / agg.orderCount) : 0
        self.statCards = [
          { label: '成交金额', value: '￥' + agg.totalAmount.toFixed(2), color: '#F56C6C' },
          { label: '订单数', value: agg.orderCount, color: '#409EFF' },
          { label: '客单价', value: '￥' + avgValue.toFixed(2), color: '#67C23A' },
          { label: '退换货率', value: (returnRate * 100).toFixed(2) + '%', color: '#E6A23C' }
        ]
      })
    },
    loadTrend() {
      var self = this
      getTrend(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var dates = []
        var amounts = []
        var counts = []
        data.forEach(function (item) {
          dates.push(item.statDate)
          amounts.push(Number(item.totalAmount) || 0)
          counts.push(Number(item.orderCount) || 0)
        })
        self.orderTrend.setOption({
          tooltip: { trigger: 'axis' },
          legend: { data: ['成交金额', '订单数'], bottom: 0 },
          grid: { left: 70, right: 60, bottom: 50 },
          xAxis: { type: 'category', data: dates },
          yAxis: [
            { type: 'value', name: '金额(￥)' },
            { type: 'value', name: '订单数' }
          ],
          series: [
            { name: '成交金额', type: 'line', smooth: true, data: amounts, itemStyle: { color: '#F56C6C' } },
            { name: '订单数', type: 'line', smooth: true, yAxisIndex: 1, data: counts, itemStyle: { color: '#409EFF' } }
          ]
        })
      })
    },
    loadCategory() {
      var self = this
      getCategory(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var pieData = []
        data.forEach(function (item) {
          pieData.push({ name: item.dimensionValue, value: Number(item.totalAmount) || 0 })
        })
        self.categoryPie.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
          legend: { type: 'scroll', bottom: 0 },
          series: [{ name: '品类销售额', type: 'pie', radius: ['35%', '65%'], data: pieData }]
        })
      })
    },
    loadReturn() {
      var self = this
      getReturnBoard(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var pieData = []
        data.forEach(function (item) {
          if (item.dimensionType === 'return_reason') {
            pieData.push({ name: item.dimensionValue, value: Number(item.returnCount) || 0 })
          }
        })
        self.returnPie.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
          legend: { type: 'scroll', bottom: 0 },
          series: [{ name: '退换货原因', type: 'pie', radius: ['35%', '65%'], data: pieData }]
        })
      })
    },
    loadPriceRange() {
      var self = this
      getPriceRange(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var names = []
        var values = []
        data.forEach(function (item) {
          names.push(item.dimensionValue)
          values.push(Number(item.orderCount) || 0)
        })
        self.priceRangeBar.setOption({
          tooltip: { trigger: 'axis' },
          grid: { left: 60, right: 20, bottom: 40 },
          xAxis: { type: 'category', data: names, axisLabel: { rotate: 30 } },
          yAxis: { type: 'value' },
          series: [{ name: '订单数', type: 'bar', barMaxWidth: 40, data: values, itemStyle: { color: '#E6A23C' } }]
        })
      })
    },
    loadHourly() {
      var self = this
      getHourly(self.getParams()).then(function (resp) {
        var data = resp.data || []
        var names = []
        var amounts = []
        var counts = []
        data.forEach(function (item) {
          names.push(item.dimensionValue)
          amounts.push(Number(item.totalAmount) || 0)
          counts.push(Number(item.orderCount) || 0)
        })
        self.hourlyBar.setOption({
          tooltip: { trigger: 'axis' },
          legend: { data: ['销售额', '订单数'], bottom: 0 },
          grid: { left: 70, right: 60, bottom: 50 },
          xAxis: { type: 'category', data: names },
          yAxis: [
            { type: 'value', name: '金额(￥)' },
            { type: 'value', name: '订单数' }
          ],
          series: [
            { name: '销售额', type: 'bar', barMaxWidth: 24, data: amounts, itemStyle: { color: '#409EFF' } },
            { name: '订单数', type: 'bar', barMaxWidth: 24, yAxisIndex: 1, data: counts, itemStyle: { color: '#67C23A' } }
          ]
        })
      })
    },
    initCharts() {
      this.orderTrend = echarts.init(document.getElementById('orderTrend'))
      this.categoryPie = echarts.init(document.getElementById('categoryPie'))
      this.returnPie = echarts.init(document.getElementById('returnPie'))
      this.priceRangeBar = echarts.init(document.getElementById('priceRangeBar'))
      this.hourlyBar = echarts.init(document.getElementById('hourlyBar'))
    },
    resizeCharts() {
      if (this.orderTrend) this.orderTrend.resize()
      if (this.categoryPie) this.categoryPie.resize()
      if (this.returnPie) this.returnPie.resize()
      if (this.priceRangeBar) this.priceRangeBar.resize()
      if (this.hourlyBar) this.hourlyBar.resize()
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
    if (this.orderTrend) this.orderTrend.dispose()
    if (this.categoryPie) this.categoryPie.dispose()
    if (this.returnPie) this.returnPie.dispose()
    if (this.priceRangeBar) this.priceRangeBar.dispose()
    if (this.hourlyBar) this.hourlyBar.dispose()
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
