<template>
  <div class="dashboard">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <div class="stat-card" :style="{borderTopColor: card.color}">
          <div class="stat-icon" :style="{background: card.color}">
            <i :class="card.icon"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value" :style="{color: card.color}">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-sub">{{ card.sub }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表行1：订单状态 + 商品状态 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header">
            <span>订单状态分布</span>
          </div>
          <div id="orderPie" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="chart-header">
            <span>商品上下架分布</span>
          </div>
          <div id="goodsPie" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表行2：会员类型 + 最近订单 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="10">
        <el-card shadow="hover">
          <div slot="header" class="chart-header">
            <span>会员类型分布</span>
          </div>
          <div id="memberPie" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card shadow="hover">
          <div slot="header" class="chart-header">
            <span>最近订单</span>
            <el-button type="text" size="mini" @click="goOrder">查看全部</el-button>
          </div>
          <el-table :data="recentOrders" size="small" style="width:100%">
            <el-table-column prop="orderNo" label="订单号" width="150"></el-table-column>
            <el-table-column prop="userName" label="会员" width="90"></el-table-column>
            <el-table-column label="实收款" width="90" align="right">
              <template slot-scope="scope">
                <span style="color:#f56c6c;">￥{{ scope.row.actualAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80" align="center">
              <template slot-scope="scope">
                <el-tag :type="getStatusTag(scope.row.orderStatus)" size="mini">
                  {{ getStatusLabel(scope.row.orderStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="150"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 销售趋势 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="chart-header">
            <span>近7天销售趋势</span>
          </div>
          <div id="salesLine" class="chart-box-wide"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { statistics, orderStatus, recentOrders, goodsStatus, memberType } from '@/api/pms_dashboard.js'

export default {
  name: 'Dashboard',
  data() {
    return {
      statData: {
        totalGoods: 0, onSaleGoods: 0,
        totalOrders: 0, pendingShip: 0,
        totalMembers: 0, blacklistMembers: 0,
        totalSales: 0
      },
      recentOrders: [],
      orderPieChart: null,
      goodsPieChart: null,
      memberPieChart: null,
      salesLineChart: null
    }
  },
  computed: {
    statCards() {
      return [
        { label: '商品总数', value: this.statData.totalGoods, sub: '上架 ' + this.statData.onSaleGoods + ' 件', color: '#409EFF', icon: 'el-icon-goods' },
        { label: '订单总数', value: this.statData.totalOrders, sub: '待发货 ' + this.statData.pendingShip + ' 单', color: '#E6A23C', icon: 'el-icon-tickets' },
        { label: '会员总数', value: this.statData.totalMembers, sub: '黑名单 ' + this.statData.blacklistMembers + ' 人', color: '#67C23A', icon: 'el-icon-user' },
        { label: '销售总额', value: '￥' + this.formatMoney(this.statData.totalSales), sub: '已完成订单', color: '#f56c6c', icon: 'el-icon-money' }
      ]
    }
  },
  methods: {
    formatMoney(val) {
      if (!val) return '0.00'
      return Number(val).toFixed(2)
    },

    /* 加载统计数据 */
    loadStatistics() {
      statistics().then(resp => {
        this.statData = resp.data || this.statData
      })
    },

    /* 加载订单状态饼图 */
    loadOrderPie() {
      orderStatus().then(resp => {
        var data = resp.data || []
        this.orderPieChart.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
          legend: { bottom: 0 },
          color: ['#E6A23C', '#409EFF', '#909399', '#67C23A', '#f56c6c'],
          series: [{
            type: 'pie',
            radius: ['40%', '65%'],
            center: ['50%', '45%'],
            label: { formatter: '{b}\n{d}%' },
            data: data
          }]
        })
      })
    },

    /* 加载商品状态饼图 */
    loadGoodsPie() {
      goodsStatus().then(resp => {
        var data = resp.data || []
        this.goodsPieChart.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
          legend: { bottom: 0 },
          color: ['#67C23A', '#909399'],
          series: [{
            type: 'pie',
            radius: ['40%', '65%'],
            center: ['50%', '45%'],
            label: { formatter: '{b}\n{d}%' },
            data: data
          }]
        })
      })
    },

    /* 加载会员类型饼图 */
    loadMemberPie() {
      memberType().then(resp => {
        var data = resp.data || []
        this.memberPieChart.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
          legend: { bottom: 0 },
          color: ['#909399', '#E6A23C', '#67C23A'],
          series: [{
            type: 'pie',
            radius: ['40%', '65%'],
            center: ['50%', '45%'],
            label: { formatter: '{b}\n{d}%' },
            data: data
          }]
        })
      })
    },

    /* 加载最近订单 */
    loadRecentOrders() {
      recentOrders().then(resp => {
        this.recentOrders = resp.data || []
      })
    },

    /* 销售趋势（模拟数据） */
    initSalesLine() {
      var days = []
      var sales = []
      var today = new Date()
      for (var i = 6; i >= 0; i--) {
        var d = new Date(today.getTime() - i * 86400000)
        days.push((d.getMonth() + 1) + '/' + d.getDate())
        sales.push(Math.round(Math.random() * 5000 + 1000))
      }
      this.salesLineChart.setOption({
        grid: { top: 30, bottom: 30, left: 50, right: 30 },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: days, boundaryGap: false },
        yAxis: { type: 'value', axisLabel: { formatter: '￥{value}' } },
        series: [{
          name: '销售额',
          data: sales,
          type: 'line',
          smooth: true,
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.3)' },
            { offset: 1, color: 'rgba(64,158,255,0.05)' }
          ])},
          lineStyle: { color: '#409EFF', width: 2 },
          itemStyle: { color: '#409EFF' }
        }]
      })
    },

    /* 订单状态 */
    getStatusLabel(status) {
      status = Number(status)
      if (status == 0) return '待付款'
      if (status == 1) return '待发货'
      if (status == 2) return '已发货'
      if (status == 3) return '已完成'
      if (status == 4) return '已取消'
      return '未知'
    },
    getStatusTag(status) {
      status = Number(status)
      if (status == 0) return 'warning'
      if (status == 1) return 'primary'
      if (status == 2) return ''
      if (status == 3) return 'success'
      if (status == 4) return 'info'
      return ''
    },

    /* 跳转订单管理 */
    goOrder() {
      this.$router.push('/orderManage')
    },

    /* 初始化图表 */
    initCharts() {
      this.orderPieChart = echarts.init(document.getElementById('orderPie'))
      this.goodsPieChart = echarts.init(document.getElementById('goodsPie'))
      this.memberPieChart = echarts.init(document.getElementById('memberPie'))
      this.salesLineChart = echarts.init(document.getElementById('salesLine'))
    },

    resizeCharts() {
      if (this.orderPieChart) this.orderPieChart.resize()
      if (this.goodsPieChart) this.goodsPieChart.resize()
      if (this.memberPieChart) this.memberPieChart.resize()
      if (this.salesLineChart) this.salesLineChart.resize()
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
      this.loadStatistics()
      this.loadOrderPie()
      this.loadGoodsPie()
      this.loadMemberPie()
      this.loadRecentOrders()
      this.initSalesLine()
    })
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
  }
}
</script>

<style scoped>
.dashboard { padding: 12px; }
.stat-row { margin-bottom: 16px; }
.stat-card {
  background: #fff;
  border-radius: 6px;
  border-top: 3px solid #409EFF;
  padding: 16px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  margin-right: 14px;
  flex-shrink: 0;
}
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; }
.stat-label { font-size: 13px; color: #606266; margin-top: 2px; }
.stat-sub { font-size: 12px; color: #909399; margin-top: 2px; }
.chart-row { margin-bottom: 16px; }
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
}
.chart-box { width: 100%; height: 260px; }
.chart-box-wide { width: 100%; height: 300px; }
</style>
