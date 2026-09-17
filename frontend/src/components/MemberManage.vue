<template>
  <div>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>会员管理</el-breadcrumb-item>
          <el-breadcrumb-item>全部会员</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 顶部统计卡片 -->
      <el-row :gutter="16" class="stat-row">
        <el-col :span="4" v-for="item in statCards" :key="item.label">
          <div class="stat-card" :style="{borderLeftColor: item.color}">
            <div class="stat-label">{{ item.label }}</div>
            <div class="stat-value" :style="{color: item.color}">{{ item.value }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 筛选区 -->
      <el-form :inline="true" :model="searchForm" style="margin-top:16px;">
        <el-form-item label="会员类型">
          <el-select v-model="searchForm.memberType" placeholder="不限会员类型" clearable style="width:140px;">
            <el-option label="普通会员" :value="1"></el-option>
            <el-option label="VIP会员" :value="2"></el-option>
            <el-option label="黄金会员" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="会员状态">
          <el-select v-model="searchForm.status" placeholder="不限会员状态" clearable style="width:130px;">
            <el-option label="正常" :value="1"></el-option>
            <el-option label="黑名单" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="来源">
          <el-select v-model="searchForm.source" placeholder="不限来源" clearable style="width:120px;">
            <el-option label="APP" value="APP"></el-option>
            <el-option label="小程序" value="小程序"></el-option>
            <el-option label="PC" value="PC"></el-option>
            <el-option label="H5" value="H5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="会员手机号">
          <el-input v-model="searchForm.phone" placeholder="会员手机号" style="width:160px;" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doSearch">搜索</el-button>
          <el-button @click="doReset">清除</el-button>
          <el-button type="success" @click="doExport" icon="el-icon-download">导出</el-button>
          <el-button @click="openLevelRule">等级规则</el-button>
        </el-form-item>
      </el-form>

      <!-- 批量操作 -->
      <div style="margin-bottom:10px;">
        <el-button type="danger" size="small" :disabled="multipleSelection.length==0" @click="handleBatchDisable">禁用</el-button>
        <span v-if="multipleSelection.length>0" style="margin-left:10px; color:#409EFF;">
          已选择 {{ multipleSelection.length }} 项
          <el-button type="text" size="mini" @click="clearSelection">清空</el-button>
        </span>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" style="width:100%" border @selection-change="handleSelectionChange" ref="memberTable">
        <el-table-column type="selection" width="45"></el-table-column>
        <el-table-column prop="id" label="会员ID" width="80"></el-table-column>
        <el-table-column label="会员头像" width="80" align="center">
          <template slot-scope="scope">
            <div class="avatar-thumb">
              <span>{{ scope.row.nickname ? scope.row.nickname.charAt(0) : '?' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="会员昵称" width="120"></el-table-column>
        <el-table-column prop="phone" label="会员手机号" width="140"></el-table-column>
        <el-table-column label="会员类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="getMemberTypeTag(scope.row.memberType)" size="mini">
              {{ getMemberTypeLabel(scope.row.memberType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="80" align="center"></el-table-column>
        <el-table-column label="会员余额" width="100" align="right">
          <template slot-scope="scope">
            <span style="color:#f56c6c;">￥{{ scope.row.balance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="积分" width="80" align="center">
          <template slot-scope="scope">
            <span style="color:#E6A23C; font-weight:bold;">{{ scope.row.points || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="会员状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status==1?'success':'danger'" size="mini">
              {{ scope.row.status==1?'正常':'黑名单' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" width="160"></el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="openView(scope.row)">查看</el-button>
            <el-button type="text" size="mini" @click="openEdit(scope.row)">编辑</el-button>
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

    <!-- 查看弹窗 -->
    <el-dialog title="会员详情" :visible.sync="viewVisible" width="600px">
      <el-tabs v-model="viewTab">
        <el-tab-pane label="基本信息" name="info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="会员ID">{{ viewData.id }}</el-descriptions-item>
            <el-descriptions-item label="会员昵称">{{ viewData.nickname }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ viewData.phone }}</el-descriptions-item>
            <el-descriptions-item label="会员类型">{{ getMemberTypeLabel(viewData.memberType) }}</el-descriptions-item>
            <el-descriptions-item label="来源">{{ viewData.source }}</el-descriptions-item>
            <el-descriptions-item label="余额">￥{{ viewData.balance }}</el-descriptions-item>
            <el-descriptions-item label="积分">{{ viewData.points || 0 }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="viewData.status==1?'success':'danger'" size="mini">
                {{ viewData.status==1?'正常':'黑名单' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间" :span="2">{{ viewData.registerTime }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane :label="'消费记录(' + consumeList.length + ')'" name="consume">
          <el-table :data="consumeList" border size="small" max-height="300">
            <el-table-column prop="orderNo" label="订单编号" width="150"></el-table-column>
            <el-table-column label="实收款" width="100">
              <template slot-scope="scope">￥{{ scope.row.actualAmount }}</template>
            </el-table-column>
            <el-table-column label="订单状态" width="90">
              <template slot-scope="scope">
                <el-tag size="mini">{{ getOrderStatusLabel(scope.row.orderStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="150"></el-table-column>
          </el-table>
          <div v-if="consumeList.length==0" style="text-align:center; color:#999; padding:20px;">暂无消费记录</div>
        </el-tab-pane>
      </el-tabs>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewVisible=false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog title="编辑会员" :visible.sync="editVisible" width="480px" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="会员昵称">
          <el-input v-model="editForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="会员类型">
          <el-select v-model="editForm.memberType" style="width:100%;">
            <el-option label="普通会员" :value="1"></el-option>
            <el-option label="VIP会员" :value="2"></el-option>
            <el-option label="黄金会员" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="会员余额">
          <el-input-number v-model="editForm.balance" :min="0" :precision="2" :step="10" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="会员积分">
          <el-input-number v-model="editForm.points" :min="0" :step="10" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="会员状态">
          <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="黑名单"></el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editVisible=false">取消</el-button>
        <el-button type="primary" @click="doUpdate">保存</el-button>
      </span>
    </el-dialog>

    <!-- 等级规则弹窗 -->
    <el-dialog title="会员等级规则配置" :visible.sync="levelRuleVisible" width="600px" :close-on-click-modal="false">
      <div style="margin-bottom:10px;">
        <el-button type="primary" size="small" @click="openAddLevelRule">+ 新增等级</el-button>
      </div>
      <el-table :data="levelRuleList" border size="small">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="levelName" label="等级名称" width="120"></el-table-column>
        <el-table-column prop="minPoints" label="所需积分" width="100"></el-table-column>
        <el-table-column label="折扣率" width="100">
          <template slot-scope="scope">{{ (scope.row.discountRate * 10).toFixed(1) }}折</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="openEditLevelRule(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" style="color:#f56c6c;" @click="doDeleteLevelRule(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 等级规则编辑子弹窗 -->
      <el-dialog :title="levelRuleForm.id ? '编辑等级' : '新增等级'" :visible.sync="levelRuleEditVisible" width="400px" :close-on-click-modal="false" append-to-body>
        <el-form :model="levelRuleForm" label-width="100px">
          <el-form-item label="等级名称" required>
            <el-input v-model="levelRuleForm.levelName"></el-input>
          </el-form-item>
          <el-form-item label="所需积分">
            <el-input-number v-model="levelRuleForm.minPoints" :min="0" :step="100" style="width:100%;"></el-input-number>
          </el-form-item>
          <el-form-item label="折扣率">
            <el-input-number v-model="levelRuleForm.discountRate" :min="0.1" :max="1" :step="0.05" :precision="2" style="width:100%;"></el-input-number>
            <div style="color:#999; font-size:12px; margin-top:4px;">1.00=无折扣，0.90=9折</div>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="levelRuleForm.remark" type="textarea" :rows="2"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="levelRuleEditVisible=false">取消</el-button>
          <el-button type="primary" @click="doSaveLevelRule">保存</el-button>
        </span>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script>
import {
  list,
  statistics,
  updateMember,
  batchDisable,
  consumeRecord,
  exportMembers,
  levelRules,
  saveLevelRule,
  deleteLevelRule
} from '@/api/pms_member.js'

export default {
  name: 'MemberManage',
  data() {
    return {
      /* 统计 */
      statData: { total: 0, blacklist: 0, normal: 0, vip: 0, gold: 0 },

      /* 搜索 */
      searchForm: {
        memberType: null,
        status: null,
        source: '',
        phone: ''
      },

      /* 表格 */
      tableData: [],
      totalCount: 0,
      curPage: 1,
      pageSize: 10,
      multipleSelection: [],

      /* 弹窗 */
      viewVisible: false,
      viewData: {},
      editVisible: false,
      editForm: {},

      /* 查看弹窗Tab */
      viewTab: 'info',
      consumeList: [],
      /* 等级规则 */
      levelRuleVisible: false,
      levelRuleList: [],
      levelRuleEditVisible: false,
      levelRuleForm: {}
    }
  },
  computed: {
    statCards() {
      return [
        { label: '会员总数', value: this.statData.total, color: '#409EFF' },
        { label: '黑名单会员数', value: this.statData.blacklist, color: '#f56c6c' },
        { label: '普通会员数', value: this.statData.normal, color: '#67C23A' },
        { label: 'A类会员数', value: this.statData.vip, color: '#E6A23C' },
        { label: 'B类会员数', value: this.statData.gold, color: '#909399' }
      ]
    }
  },
  methods: {
    /* 加载统计 */
    loadStatistics() {
      statistics().then(resp => {
        this.statData = resp.data || this.statData
      })
    },

    /* 搜索 */
    doSearch() {
      this.curPage = 1
      this.loadList()
    },

    /* 重置 */
    doReset() {
      this.searchForm = {
        memberType: null,
        status: null,
        source: '',
        phone: ''
      }
      this.curPage = 1
      this.loadList()
    },

    /* 加载列表 */
    loadList() {
      var params = {
        memberType: this.searchForm.memberType,
        status: this.searchForm.status,
        source: this.searchForm.source || null,
        phone: this.searchForm.phone || null
      }
      list(this.curPage, this.pageSize, params).then(resp => {
        var data = resp.data || []
        data.forEach(function(item) {
          item.status = Number(item.status)
          item.memberType = Number(item.memberType)
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

    /* 会员类型标签 */
    getMemberTypeLabel(type) {
      if (type == 1) return '普通会员'
      if (type == 2) return 'VIP会员'
      if (type == 3) return '黄金会员'
      return '未知'
    },
    getMemberTypeTag(type) {
      if (type == 1) return 'info'
      if (type == 2) return 'warning'
      if (type == 3) return 'success'
      return ''
    },

    /* 查看 */
    openView(row) {
      this.viewData = row
      this.viewTab = 'info'
      this.consumeList = []
      this.viewVisible = true
      consumeRecord(row.id).then(resp => {
        this.consumeList = resp.data || []
      })
    },

    /* 编辑 */
    openEdit(row) {
      this.editForm = {
        id: row.id,
        nickname: row.nickname,
        phone: row.phone,
        memberType: Number(row.memberType),
        balance: row.balance,
        points: row.points || 0,
        status: Number(row.status)
      }
      this.editVisible = true
    },

    /* 保存编辑 */
    doUpdate() {
      updateMember(this.editForm).then(() => {
        this.$message.success('更新成功')
        this.editVisible = false
        this.loadList()
        this.loadStatistics()
      })
    },

    /* 批量禁用 */
    handleBatchDisable() {
      var ids = this.multipleSelection.map(function(item) { return item.id })
      this.$confirm('确认将选中的 ' + ids.length + ' 个会员设为黑名单？', '警告', {
        type: 'warning'
      }).then(() => {
        batchDisable(ids).then(() => {
          this.$message.success('批量禁用成功')
          this.clearSelection()
          this.loadList()
          this.loadStatistics()
        })
      }).catch(() => {})
    },

    /* 清空选择 */
    clearSelection() {
      this.$refs.memberTable.clearSelection()
    },

    /* 复选框 */
    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    /* 订单状态文字 */
    getOrderStatusLabel(status) {
      if (status == 0) return '待付款'
      if (status == 1) return '待发货'
      if (status == 2) return '已发货'
      if (status == 3) return '已完成'
      if (status == 4) return '已取消'
      return '未知'
    },

    /* 导出 */
    doExport() {
      var params = {
        memberType: this.searchForm.memberType,
        status: this.searchForm.status,
        source: this.searchForm.source || null,
        phone: this.searchForm.phone || null
      }
      this.$message.info('正在导出，请稍候...')
      exportMembers(params).then(blob => {
        var url = window.URL.createObjectURL(new Blob([blob]))
        var link = document.createElement('a')
        link.href = url
        link.setAttribute('download', '会员列表.csv')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      }).catch(() => {
        this.$message.error('导出失败')
      })
    },

    /* 打开等级规则 */
    openLevelRule() {
      this.levelRuleVisible = true
      this.loadLevelRules()
    },

    /* 加载等级规则 */
    loadLevelRules() {
      levelRules().then(resp => {
        this.levelRuleList = resp.data || []
      })
    },

    /* 新增等级规则 */
    openAddLevelRule() {
      this.levelRuleForm = {
        levelName: '',
        minPoints: 0,
        discountRate: 1.00,
        remark: ''
      }
      this.levelRuleEditVisible = true
    },

    /* 编辑等级规则 */
    openEditLevelRule(row) {
      this.levelRuleForm = {
        id: row.id,
        levelName: row.levelName,
        minPoints: row.minPoints,
        discountRate: row.discountRate,
        remark: row.remark || ''
      }
      this.levelRuleEditVisible = true
    },

    /* 保存等级规则 */
    doSaveLevelRule() {
      if (!this.levelRuleForm.levelName || !this.levelRuleForm.levelName.trim()) {
        this.$message.warning('请输入等级名称')
        return
      }
      saveLevelRule(this.levelRuleForm).then(() => {
        this.$message.success('保存成功')
        this.levelRuleEditVisible = false
        this.loadLevelRules()
      })
    },

    /* 删除等级规则 */
    doDeleteLevelRule(row) {
      this.$confirm('确认删除等级【' + row.levelName + '】？', '提示', {
        type: 'warning'
      }).then(() => {
        deleteLevelRule(row.id).then(() => {
          this.$message.success('删除成功')
          this.loadLevelRules()
        })
      }).catch(() => {})
    }
  },
  created() {
    this.loadStatistics()
    this.loadList()
  }
}
</script>

<style scoped>
.box-card { margin: 12px; }
.stat-row { margin-bottom: 8px; }
.stat-card {
  background: #fafafa;
  border: 1px solid #ebeef5;
  border-left: 4px solid #409EFF;
  border-radius: 4px;
  padding: 14px 16px;
  text-align: center;
}
.stat-label { font-size: 13px; color: #909399; margin-bottom: 6px; }
.stat-value { font-size: 24px; font-weight: bold; }
.avatar-thumb {
  width: 36px;
  height: 36px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  margin: 0 auto;
}
</style>
