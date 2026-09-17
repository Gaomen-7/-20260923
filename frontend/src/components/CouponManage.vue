<template>
  <div>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>活动管理</el-breadcrumb-item>
          <el-breadcrumb-item>优惠券管理</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 筛选区 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="是否有效">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:110px;">
            <el-option label="有效" :value="1"></el-option>
            <el-option label="无效" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width:240px;">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="优惠券名称">
          <el-input v-model="searchForm.couponName" placeholder="请输入名称" style="width:160px;" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doSearch">搜索</el-button>
          <el-button @click="doReset">清空</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div style="margin-bottom:10px;">
        <el-button type="primary" size="small" @click="openAdd">+ 添加</el-button>
        <el-button type="danger" size="small" :disabled="multipleSelection.length==0" @click="handleBatchDelete">删除</el-button>
        <span style="color:#999; margin-left:10px;">已选 {{ multipleSelection.length }} 项</span>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" style="width:100%" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="45"></el-table-column>
        <el-table-column prop="id" label="编号" width="70"></el-table-column>
        <el-table-column prop="couponName" label="优惠券名称" width="160"></el-table-column>
        <el-table-column label="优惠金额" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.couponType==1" type="danger" size="mini">满减￥{{ scope.row.discountAmount }}</el-tag>
            <el-tag v-else type="warning" size="mini">{{ getDiscountLabel(scope.row.discountRate) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠时间" width="180">
          <template slot-scope="scope">
            {{ scope.row.startTime }} ~ {{ scope.row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="发放数量" width="90" align="center"></el-table-column>
        <el-table-column label="已领取" width="90" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.receivedCount }}</span>
            <div style="color:#999; font-size:11px;">
              {{ getReceivePercent(scope.row) }}%
            </div>
          </template>
        </el-table-column>
        <el-table-column label="是否有效" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status==1?'success':'info'" size="mini">
              {{ scope.row.status==1?'是':'否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisher" label="发布者" width="90"></el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="150"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="openEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" @click="openBind(scope.row)">绑定商品</el-button>
            <el-button type="text" size="mini" style="color:#f56c6c;" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount"
        :current-page="curPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        style="margin-top:12px; text-align:right;">
      </el-pagination>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px" :close-on-click-modal="false">
      <el-form :model="couponForm" label-width="100px" ref="couponForm">
        <el-form-item label="优惠券名称" required>
          <el-input v-model="couponForm.couponName" placeholder="请输入优惠券名称"></el-input>
        </el-form-item>
        <el-form-item label="优惠类型">
          <el-radio-group v-model="couponForm.couponType">
            <el-radio :label="1">满减</el-radio>
            <el-radio :label="2">折扣</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="couponForm.couponType==1" label="优惠金额">
          <el-input-number v-model="couponForm.discountAmount" :min="0" :precision="2" :step="1" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item v-if="couponForm.couponType==2" label="折扣率">
          <el-input-number v-model="couponForm.discountRate" :min="0.1" :max="1" :step="0.05" :precision="2" style="width:100%;"></el-input-number>
          <div style="color:#999; font-size:12px; margin-top:4px;">0.90 表示9折（即优惠10%）</div>
        </el-form-item>
        <el-form-item label="使用门槛">
          <el-input-number v-model="couponForm.minAmount" :min="0" :precision="2" :step="10" style="width:100%;"></el-input-number>
          <div style="color:#999; font-size:12px; margin-top:4px;">满多少元可用，0表示无门槛</div>
        </el-form-item>
        <el-form-item label="优惠时间">
          <el-date-picker
            v-model="couponTimeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width:100%;">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发放数量">
          <el-input-number v-model="couponForm.totalCount" :min="0" :step="10" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="是否有效">
          <el-switch v-model="couponForm.status" :active-value="1" :inactive-value="0" active-text="有效" inactive-text="无效"></el-switch>
        </el-form-item>
        <el-form-item label="发布者">
          <el-input v-model="couponForm.publisher" placeholder="请输入发布者"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="doSave">确定</el-button>
      </span>
    </el-dialog>

    <!-- 绑定商品弹窗 -->
    <el-dialog title="绑定商品" :visible.sync="bindVisible" width="700px" :close-on-click-modal="false">
      <div style="margin-bottom:12px;">
        <el-tag type="info">优惠券：{{ bindCouponName }}</el-tag>
        <span style="margin-left:10px; color:#999; font-size:12px;">勾选商品后点击"批量绑定"，sku_id为空表示绑定整个SPU</span>
      </div>

      <!-- 商品选择区 -->
      <el-card shadow="never" style="margin-bottom:12px;">
        <div slot="header">选择商品（SPU）</div>
        <el-table :data="goodsSelectList" border size="small" @selection-change="handleGoodsSelect">
          <el-table-column type="selection" width="45"></el-table-column>
          <el-table-column prop="id" label="ID" width="70"></el-table-column>
          <el-table-column prop="goodsName" label="商品名称"></el-table-column>
          <el-table-column prop="price" label="价格" width="100">
            <template slot-scope="scope">￥{{ scope.row.price }}</template>
          </el-table-column>
        </el-table>
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="goodsTotal"
          :current-page="goodsPage"
          :page-size="5"
          @current-change="handleGoodsPage"
          style="margin-top:8px; text-align:right;">
        </el-pagination>
      </el-card>

      <div style="margin-bottom:12px;">
        <el-button type="primary" size="small" :disabled="selectedGoodsIds.length==0" @click="doBindGoods">
          批量绑定选中商品({{ selectedGoodsIds.length }})
        </el-button>
      </div>

      <!-- 已绑定列表 -->
      <el-card shadow="never">
        <div slot="header">已绑定商品</div>
        <el-table :data="boundList" border size="small">
          <el-table-column prop="goodsId" label="商品ID" width="80"></el-table-column>
          <el-table-column prop="goodsName" label="商品名称" width="180"></el-table-column>
          <el-table-column prop="skuName" label="SKU范围" width="140"></el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="scope">
              <el-button type="text" size="mini" style="color:#f56c6c;" @click="doUnbind(scope.row)">解绑</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="boundList.length==0" style="text-align:center; color:#999; padding:20px;">
          暂无绑定商品，当前为全场通用
        </div>
      </el-card>
    </el-dialog>
  </div>
</template>

<script>
import {
  list,
  addCoupon,
  updateCoupon,
  deleteCoupon,
  batchDelete,
  bindGoods,
  unbindGoods,
  boundGoods
} from '@/api/pms_coupon.js'
import { getGoodsList } from '@/api/pms_goods.js'

export default {
  name: 'CouponManage',
  data() {
    return {
      /* 搜索 */
      searchForm: {
        couponName: '',
        status: null,
        startTime: '',
        endTime: ''
      },
      dateRange: [],

      /* 表格 */
      tableData: [],
      totalCount: 0,
      curPage: 1,
      pageSize: 10,
      multipleSelection: [],

      /* 弹窗 */
      dialogVisible: false,
      dialogTitle: '',
      opType: '',
      couponForm: {},
      couponTimeRange: [],

      /* 绑定商品弹窗 */
      bindVisible: false,
      bindCouponId: null,
      bindCouponName: '',
      goodsSelectList: [],
      goodsTotal: 0,
      goodsPage: 1,
      selectedGoodsIds: [],
      boundList: []
    }
  },
  methods: {
    /* 搜索 */
    doSearch() {
      this.curPage = 1
      if (this.dateRange && this.dateRange.length == 2) {
        this.searchForm.startTime = this.dateRange[0]
        this.searchForm.endTime = this.dateRange[1]
      } else {
        this.searchForm.startTime = ''
        this.searchForm.endTime = ''
      }
      this.loadList()
    },

    /* 重置 */
    doReset() {
      this.searchForm = {
        couponName: '',
        status: null,
        startTime: '',
        endTime: ''
      }
      this.dateRange = []
      this.curPage = 1
      this.loadList()
    },

    /* 加载列表 */
    loadList() {
      var params = {
        couponName: this.searchForm.couponName || null,
        status: this.searchForm.status,
        startTime: this.searchForm.startTime || null,
        endTime: this.searchForm.endTime || null
      }
      list(this.curPage, this.pageSize, params).then(resp => {
        var data = resp.data || []
        data.forEach(function(item) {
          item.status = Number(item.status)
          item.couponType = Number(item.couponType)
        })
        this.tableData = data
        this.totalCount = resp.total || 0
      })
    },

    /* 分页 */
    handlePageChange(page) {
      this.curPage = page
      this.loadList()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.curPage = 1
      this.loadList()
    },

    /* 折扣率显示：0.90 → 9折 */
    getDiscountLabel(rate) {
      if (!rate) return ''
      return (rate * 10).toFixed(1) + '折'
    },

    /* 领取百分比 */
    getReceivePercent(row) {
      if (!row.totalCount || row.totalCount == 0) return 0
      return Math.round((row.receivedCount / row.totalCount) * 100)
    },

    /* 打开新增 */
    openAdd() {
      this.opType = 'add'
      this.dialogTitle = '添加优惠券'
      this.couponForm = {
        couponName: '',
        couponType: 1,
        discountAmount: 0,
        discountRate: 0.90,
        minAmount: 0,
        totalCount: 100,
        status: 1,
        publisher: 'admin'
      }
      this.couponTimeRange = []
      this.dialogVisible = true
    },

    /* 打开编辑 */
    openEdit(row) {
      this.opType = 'edit'
      this.dialogTitle = '编辑优惠券【' + row.couponName + '】'
      this.couponForm = {
        id: row.id,
        couponName: row.couponName,
        couponType: Number(row.couponType),
        discountAmount: row.discountAmount || 0,
        discountRate: row.discountRate || 0.90,
        minAmount: row.minAmount || 0,
        totalCount: row.totalCount,
        receivedCount: row.receivedCount,
        status: Number(row.status),
        publisher: row.publisher
      }
      if (row.startTime && row.endTime) {
        this.couponTimeRange = [row.startTime, row.endTime]
      } else {
        this.couponTimeRange = []
      }
      this.dialogVisible = true
    },

    /* 保存 */
    doSave() {
      if (!this.couponForm.couponName || !this.couponForm.couponName.trim()) {
        this.$message.warning('请输入优惠券名称')
        return
      }
      if (this.couponTimeRange && this.couponTimeRange.length == 2) {
        this.couponForm.startTime = this.couponTimeRange[0]
        this.couponForm.endTime = this.couponTimeRange[1]
      }
      if (this.opType == 'add') {
        addCoupon(this.couponForm).then(() => {
          this.$message.success('添加成功')
          this.dialogVisible = false
          this.loadList()
        })
      } else {
        updateCoupon(this.couponForm).then(() => {
          this.$message.success('更新成功')
          this.dialogVisible = false
          this.loadList()
        })
      }
    },

    /* 单个删除 */
    handleDelete(row) {
      this.$confirm('确认删除优惠券【' + row.couponName + '】？', '提示', {
        type: 'warning'
      }).then(() => {
        deleteCoupon(row.id).then(() => {
          this.$message.success('删除成功')
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 批量删除 */
    handleBatchDelete() {
      var ids = this.multipleSelection.map(function(item) { return item.id })
      this.$confirm('确认删除选中的 ' + ids.length + ' 张优惠券？', '警告', {
        type: 'error'
      }).then(() => {
        batchDelete(ids).then(() => {
          this.$message.success('批量删除成功')
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 复选框 */
    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    /* 打开绑定弹窗 */
    openBind(row) {
      this.bindCouponId = row.id
      this.bindCouponName = row.couponName
      this.selectedGoodsIds = []
      this.goodsPage = 1
      this.bindVisible = true
      this.loadGoodsSelect()
      this.loadBoundList()
    },

    /* 加载商品选择列表 */
    loadGoodsSelect() {
      getGoodsList({ page: this.goodsPage, limit: 5 }).then(resp => {
        var data = resp.data || {}
        this.goodsSelectList = data.data || data || []
        this.goodsTotal = data.total || resp.total || 0
      })
    },

    /* 商品分页 */
    handleGoodsPage(page) {
      this.goodsPage = page
      this.loadGoodsSelect()
    },

    /* 商品选中 */
    handleGoodsSelect(val) {
      this.selectedGoodsIds = val.map(function(item) { return item.id })
    },

    /* 批量绑定商品 */
    doBindGoods() {
      bindGoods(this.bindCouponId, this.selectedGoodsIds, null).then(() => {
        this.$message.success('绑定成功')
        this.selectedGoodsIds = []
        this.loadBoundList()
      })
    },

    /* 加载已绑定列表 */
    loadBoundList() {
      boundGoods(this.bindCouponId).then(resp => {
        this.boundList = resp.data || []
      })
    },

    /* 解绑 */
    doUnbind(row) {
      this.$confirm('确认解绑商品【' + row.goodsName + '】？', '提示', {
        type: 'warning'
      }).then(() => {
        unbindGoods(this.bindCouponId, row.goodsId, row.skuId).then(() => {
          this.$message.success('解绑成功')
          this.loadBoundList()
        })
      }).catch(() => {})
    }
  },
  created() {
    this.loadList()
  }
}
</script>

<style scoped>
.box-card { margin: 12px; }
</style>
