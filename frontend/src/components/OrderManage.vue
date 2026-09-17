<template>
  <div>
    <el-card class="box-card">
      <!-- 筛选区 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号" style="width:160px;" clearable></el-input>
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.goodsName" placeholder="请输入商品名称" style="width:160px;" clearable></el-input>
        </el-form-item>
        <el-form-item label="创建时间">
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
        <el-form-item label="会员昵称">
          <el-input v-model="searchForm.userName" placeholder="请输入会员昵称" style="width:130px;" clearable></el-input>
        </el-form-item>
        <el-form-item label="收货人">
          <el-input v-model="searchForm.receiverName" placeholder="请输入收货人" style="width:130px;" clearable></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.receiverPhone" placeholder="收货手机号" style="width:130px;" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doSearch">搜索</el-button>
          <el-button @click="doReset">重置</el-button>
          <el-button type="success" @click="doExport" icon="el-icon-download">导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 状态 Tab -->
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部订单" name="all"></el-tab-pane>
        <el-tab-pane :label="'待付款(' + statusCount[0] + ')'" name="0"></el-tab-pane>
        <el-tab-pane :label="'待发货(' + statusCount[1] + ')'" name="1"></el-tab-pane>
        <el-tab-pane label="已发货" name="2"></el-tab-pane>
        <el-tab-pane label="已完成" name="3"></el-tab-pane>
        <el-tab-pane label="已取消" name="4"></el-tab-pane>
      </el-tabs>

      <!-- 批量操作栏 -->
      <div style="margin-bottom:8px;">
        <el-button type="danger" size="small" :disabled="selectedIds.length==0" @click="doBatchDelete" icon="el-icon-delete">
          批量删除({{ selectedIds.length }})
        </el-button>
        <span style="margin-left:10px; color:#999; font-size:12px;">仅已完成/已取消订单可删除</span>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" style="width:100%" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="45"></el-table-column>
        <el-table-column prop="orderNo" label="订单编号" width="150"></el-table-column>
        <el-table-column label="商品" min-width="200">
          <template slot-scope="scope">
            <div style="display:flex; align-items:center;">
              <div style="width:50px; height:50px; background:#f0f0f0; margin-right:10px; display:flex; align-items:center; justify-content:center; color:#999; font-size:12px; flex-shrink:0;">
                商品图
              </div>
              <div>
                <div style="font-size:13px;">{{ scope.row.goodsName }}</div>
                <div style="color:#999; font-size:12px; margin-top:2px;">
                  ￥{{ scope.row.price }} × {{ scope.row.quantity }}
                  <span v-if="scope.row.itemCount > 1" style="color:#409EFF;"> 等{{ scope.row.itemCount }}件</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="80">
          <template slot-scope="scope">￥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column label="数量" width="60">
          <template slot-scope="scope">{{ scope.row.quantity }}</template>
        </el-table-column>
        <el-table-column label="实收款" width="90">
          <template slot-scope="scope">
            <span style="color:#f56c6c; font-weight:bold;">￥{{ scope.row.actualAmount }}</span>
            <div v-if="scope.row.freight > 0" style="color:#999; font-size:11px;">运费:￥{{ scope.row.freight }}</div>
          </template>
        </el-table-column>
        <el-table-column label="会员" width="130">
          <template slot-scope="scope">
            <div>{{ scope.row.userName }}</div>
            <div style="color:#999; font-size:12px;">{{ scope.row.userPhone }}</div>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.orderStatus)" size="mini">
              {{ scope.row.orderStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="付款状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.payStatus==1?'success':'warning'" size="mini">
              {{ scope.row.payStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="150"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="openDetail(scope.row)">订单详情</el-button>
            <!-- 待付款：修改价格 + 取消 -->
            <template v-if="scope.row.orderStatus==0">
              <el-button type="text" size="mini" @click="openPriceDialog(scope.row)">修改价格</el-button>
              <el-button type="text" size="mini" style="color:#f56c6c;" @click="doCancel(scope.row)">取消订单</el-button>
            </template>
            <!-- 待发货：发货 + 取消 -->
            <template v-if="scope.row.orderStatus==1">
              <el-button type="text" size="mini" style="color:#67c23a;" @click="doShip(scope.row)">发货</el-button>
              <el-button type="text" size="mini" style="color:#f56c6c;" @click="doCancel(scope.row)">取消订单</el-button>
            </template>
            <!-- 已发货：查看物流 + 取消（回滚库存） -->
            <template v-if="scope.row.orderStatus==2">
              <el-button type="text" size="mini" @click="viewLogistics(scope.row)">查看物流</el-button>
              <el-button type="text" size="mini" style="color:#f56c6c;" @click="doCancel(scope.row)">取消订单</el-button>
            </template>
            <!-- 已完成/已取消：删除 -->
            <template v-if="scope.row.orderStatus==3 || scope.row.orderStatus==4">
              <el-button type="text" size="mini" style="color:#f56c6c;" @click="doDelete(scope.row)">删除</el-button>
            </template>
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

    <!-- 修改价格弹窗 -->
    <el-dialog title="修改价格" :visible.sync="priceDialogVisible" width="400px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="订单号">
          <span>{{ priceForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="原实收款">
          <span style="color:#999;">￥{{ priceForm.oldAmount }}</span>
        </el-form-item>
        <el-form-item label="新实收款" required>
          <el-input-number v-model="priceForm.newAmount" :min="0" :precision="2" :step="1" style="width:100%;"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="priceDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="doUpdatePrice">确定</el-button>
      </span>
    </el-dialog>

    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" :visible.sync="detailDialogVisible" width="700px" :close-on-click-modal="false">
      <div v-if="detailData">
        <!-- 订单基本信息 -->
        <el-descriptions :column="2" border size="small" style="margin-bottom:16px;">
          <el-descriptions-item label="订单编号">{{ detailData.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ detailData.createTime }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusTagType(detailData.orderStatus)" size="mini">{{ detailData.orderStatusName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="付款状态">
            <el-tag :type="detailData.payStatus==1?'success':'warning'" size="mini">{{ detailData.payStatusName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="会员">{{ detailData.userName }}（{{ detailData.userPhone }}）</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ detailData.receiverName }}（{{ detailData.receiverPhone }}）</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ detailData.receiverAddress }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            <div v-if="!remarkEditing">
              <span>{{ detailData.remark || '无' }}</span>
              <el-button type="text" size="mini" @click="startEditRemark" style="margin-left:8px;">编辑</el-button>
            </div>
            <div v-else>
              <el-input v-model="editRemarkText" type="textarea" :rows="2" placeholder="请输入备注" style="margin-bottom:6px;"></el-input>
              <el-button type="primary" size="mini" @click="saveRemark">保存</el-button>
              <el-button size="mini" @click="remarkEditing=false">取消</el-button>
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 商品明细 -->
        <h4 style="margin:10px 0;">商品明细</h4>
        <el-table :data="detailData.itemList" border size="small">
          <el-table-column prop="goodsName" label="商品名称"></el-table-column>
          <el-table-column prop="price" label="单价" width="100">
            <template slot-scope="scope">￥{{ scope.row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
          <el-table-column prop="subtotal" label="小计" width="100">
            <template slot-scope="scope">￥{{ scope.row.subtotal }}</template>
          </el-table-column>
        </el-table>

        <!-- 金额汇总 -->
        <div style="margin-top:12px; text-align:right; line-height:1.8;">
          <div>商品总额：￥{{ detailData.totalAmount }}</div>
          <div>运费：￥{{ detailData.freight }}</div>
          <div v-if="detailData.couponId" style="color:#67C23A;">
            优惠券：-￥{{ detailData.discountAmount }}（{{ detailData.couponName || '已使用优惠券' }}）
          </div>
          <div v-if="detailData.orderStatus==0 && !detailData.couponId" style="margin-top:8px;">
            <el-select v-model="selectedCouponId" placeholder="选择优惠券" size="small" style="width:200px;" clearable>
              <el-option v-for="c in couponList" :key="c.id" :label="c.couponName + (c.couponType==1?' [满减'+c.discountAmount+']':' ['+(c.discountRate*10).toFixed(1)+'折]')" :value="c.id"></el-option>
            </el-select>
            <el-button type="primary" size="small" :disabled="!selectedCouponId" @click="doApplyCoupon" style="margin-left:8px;">使用</el-button>
          </div>
          <div>折扣：-￥{{ detailData.discountAmount }}</div>
          <div style="font-size:16px; color:#f56c6c; font-weight:bold;">实收款：￥{{ detailData.actualAmount }}</div>
        </div>

        <!-- 状态流转操作 -->
        <div style="margin-top:16px; text-align:center; border-top:1px solid #eee; padding-top:12px;">
          <el-button v-if="detailData.orderStatus==0" type="warning" @click="doCancelFromDetail">取消订单</el-button>
          <el-button v-if="detailData.orderStatus==1" type="success" @click="doShipFromDetail">确认发货</el-button>
          <el-button v-if="detailData.orderStatus==1" type="warning" @click="doCancelFromDetail">取消订单</el-button>
          <el-button v-if="detailData.orderStatus==2" type="warning" @click="doCancelFromDetail">取消订单（回滚库存）</el-button>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible=false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  list,
  detail,
  ship,
  updatePrice,
  cancel,
  deleteOrder,
  updateRemark,
  batchDelete,
  exportOrders,
  availableCoupons
} from '@/api/pms_order.js'
import { applyToOrder } from '@/api/pms_coupon.js'

export default {
  name: 'OrderManage',
  data() {
    return {
      /* 搜索 */
      searchForm: {
        orderNo: '',
        goodsName: '',
        keyword: '',
        userName: '',
        receiverName: '',
        receiverPhone: '',
        orderStatus: null,
        startTime: '',
        endTime: ''
      },
      dateRange: [],

      /* 状态 Tab */
      activeTab: 'all',
      statusCount: [0, 0],  // 待付款、待发货数量

      /* 表格 */
      tableData: [],
      totalCount: 0,
      curPage: 1,
      pageSize: 10,

      /* 修改价格弹窗 */
      priceDialogVisible: false,
      priceForm: {
        id: null,
        orderNo: '',
        oldAmount: 0,
        newAmount: 0
      },

      /* 详情弹窗 */
      detailDialogVisible: false,
      detailData: null,

      /* 批量选中 */
      selectedIds: [],
      /* 备注编辑 */
      remarkEditing: false,
      editRemarkText: '',
      /* 优惠券 */
      couponList: [],
      selectedCouponId: null
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
        orderNo: '',
        goodsName: '',
        keyword: '',
        userName: '',
        receiverName: '',
        receiverPhone: '',
        orderStatus: null,
        startTime: '',
        endTime: ''
      }
      this.dateRange = []
      this.activeTab = 'all'
      this.curPage = 1
      this.loadList()
    },

    /* Tab 切换 */
    handleTabClick() {
      if (this.activeTab == 'all') {
        this.searchForm.orderStatus = null
      } else {
        this.searchForm.orderStatus = parseInt(this.activeTab)
      }
      this.curPage = 1
      this.loadList()
    },

    /* 加载列表 */
    loadList() {
      var params = {
        orderNo: this.searchForm.orderNo || null,
        goodsName: this.searchForm.goodsName || null,
        keyword: this.searchForm.keyword || null,
        userName: this.searchForm.userName || null,
        receiverName: this.searchForm.receiverName || null,
        receiverPhone: this.searchForm.receiverPhone || null,
        orderStatus: this.searchForm.orderStatus,
        startTime: this.searchForm.startTime || null,
        endTime: this.searchForm.endTime || null
      }
      list(this.curPage, this.pageSize, params).then(resp => {
        this.tableData = resp.data || []
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

    /* 状态标签颜色 */
    getStatusTagType(status) {
      switch (status) {
        case 0: return 'warning'  // 待付款
        case 1: return 'primary'  // 待发货
        case 2: return ''         // 已发货
        case 3: return 'success'  // 已完成
        case 4: return 'info'     // 已取消
        default: return ''
      }
    },

    /* 打开详情 */
    openDetail(row) {
      detail(row.id).then(resp => {
        this.detailData = resp.data
        this.detailDialogVisible = true
        this.remarkEditing = false
        this.selectedCouponId = null
        this.loadCoupons()
      })
    },

    /* 发货 */
    doShip(row) {
      this.$confirm('确认订单【' + row.orderNo + '】已发货？', '提示', {
        type: 'warning'
      }).then(() => {
        ship(row.id).then(() => {
          this.$message.success('发货成功')
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 从详情弹窗发货 */
    doShipFromDetail() {
      var id = this.detailData.id
      ship(id).then(() => {
        this.$message.success('发货成功')
        this.detailDialogVisible = false
        this.loadList()
      })
    },

    /* 取消订单 */
    doCancel(row) {
      this.$confirm('确认取消订单【' + row.orderNo + '】？', '提示', {
        type: 'warning'
      }).then(() => {
        cancel(row.id).then(() => {
          this.$message.success('取消成功')
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 从详情弹窗取消 */
    doCancelFromDetail() {
      var id = this.detailData.id
      this.$confirm('确认取消该订单？', '提示', {
        type: 'warning'
      }).then(() => {
        cancel(id).then(() => {
          this.$message.success('取消成功')
          this.detailDialogVisible = false
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 删除 */
    doDelete(row) {
      this.$confirm('确认删除订单【' + row.orderNo + '】？删除后不可恢复。', '警告', {
        type: 'error'
      }).then(() => {
        deleteOrder(row.id).then(() => {
          this.$message.success('删除成功')
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 查看物流（功能开发中） */
    viewLogistics(row) {
      this.$message.info('物流查询功能开发中')
    },

    /* 打开修改价格弹窗 */
    openPriceDialog(row) {
      this.priceForm = {
        id: row.id,
        orderNo: row.orderNo,
        oldAmount: row.actualAmount,
        newAmount: row.actualAmount
      }
      this.priceDialogVisible = true
    },

    /* 确认修改价格 */
    doUpdatePrice() {
      if (this.priceForm.newAmount == null || this.priceForm.newAmount < 0) {
        this.$message.warning('请输入合法的价格')
        return
      }
      updatePrice(this.priceForm.id, this.priceForm.newAmount).then(() => {
        this.$message.success('价格修改成功')
        this.priceDialogVisible = false
        this.loadList()
      })
    },

    /* 表格选中变化 */
    handleSelectionChange(selection) {
      this.selectedIds = selection.map(item => item.id)
    },

    /* 批量删除 */
    doBatchDelete() {
      if (this.selectedIds.length == 0) {
        this.$message.warning('请先选择要删除的订单')
        return
      }
      this.$confirm('确认删除选中的 ' + this.selectedIds.length + ' 条订单？仅已完成/已取消状态可删除，删除后不可恢复。', '警告', {
        type: 'error'
      }).then(() => {
        batchDelete(this.selectedIds).then(() => {
          this.$message.success('批量删除成功')
          this.selectedIds = []
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 导出 */
    doExport() {
      if (this.dateRange && this.dateRange.length == 2) {
        this.searchForm.startTime = this.dateRange[0]
        this.searchForm.endTime = this.dateRange[1]
      }
      var params = {
        orderNo: this.searchForm.orderNo || null,
        goodsName: this.searchForm.goodsName || null,
        keyword: this.searchForm.keyword || null,
        userName: this.searchForm.userName || null,
        receiverName: this.searchForm.receiverName || null,
        receiverPhone: this.searchForm.receiverPhone || null,
        orderStatus: this.searchForm.orderStatus,
        startTime: this.searchForm.startTime || null,
        endTime: this.searchForm.endTime || null
      }
      this.$message.info('正在导出，请稍候...')
      exportOrders(params).then(blob => {
        /* blob 下载 */
        var url = window.URL.createObjectURL(new Blob([blob]))
        var link = document.createElement('a')
        link.href = url
        link.setAttribute('download', '订单列表.csv')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      }).catch(() => {
        this.$message.error('导出失败')
      })
    },

    /* 开始编辑备注 */
    startEditRemark() {
      this.editRemarkText = this.detailData.remark || ''
      this.remarkEditing = true
    },

    /* 保存备注 */
    saveRemark() {
      updateRemark(this.detailData.id, this.editRemarkText).then(() => {
        this.$message.success('备注修改成功')
        this.detailData.remark = this.editRemarkText
        this.remarkEditing = false
      })
    },

    /* 加载可用优惠券 */
    loadCoupons() {
      availableCoupons().then(resp => {
        this.couponList = resp.data || []
      })
    },

    /* 使用优惠券 */
    doApplyCoupon() {
      if (!this.selectedCouponId) {
        this.$message.warning('请选择优惠券')
        return
      }
      this.$confirm('确认使用该优惠券？将重新计算订单实收款。', '提示', {
        type: 'warning'
      }).then(() => {
        applyToOrder(this.detailData.id, this.selectedCouponId).then(() => {
          this.$message.success('优惠券使用成功')
          this.selectedCouponId = null
          /* 刷新详情 */
          detail(this.detailData.id).then(resp => {
            this.detailData = resp.data
          })
          this.loadList()
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
