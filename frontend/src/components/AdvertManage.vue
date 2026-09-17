<template>
  <div>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>广告设置</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 筛选区 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="广告名称">
          <el-input v-model="searchForm.advertName" placeholder="请输入广告名称" style="width:160px;" clearable></el-input>
        </el-form-item>
        <el-form-item label="投放位置">
          <el-select v-model="searchForm.position" placeholder="全部位置" clearable style="width:140px;">
            <el-option label="首页轮播" value="首页轮播"></el-option>
            <el-option label="分类页" value="分类页"></el-option>
            <el-option label="商品详情页" value="商品详情页"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width:120px;">
            <el-option label="投放中" :value="1"></el-option>
            <el-option label="已下线" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doSearch">搜索</el-button>
          <el-button @click="doReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div style="margin-bottom:10px;">
        <el-button type="primary" size="small" @click="openAdd">+ 添加广告</el-button>
        <el-button type="danger" size="small" :disabled="multipleSelection.length==0" @click="handleBatchDelete">批量删除</el-button>
        <el-button type="warning" size="small" @click="doRefreshExpired" icon="el-icon-refresh">刷新过期状态</el-button>
        <span v-if="multipleSelection.length>0" style="margin-left:10px; color:#409EFF;">
          已选 {{ multipleSelection.length }} 项
        </span>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" style="width:100%" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="45"></el-table-column>
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="advertName" label="广告名称" width="150" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column label="广告形式" width="90">
          <template slot-scope="scope">
            <el-tag :type="getTypeTag(scope.row.advertType)" size="mini">
              {{ getTypeLabel(scope.row.advertType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="position" label="投放位置" width="110"></el-table-column>
        <el-table-column label="计费方式" width="90" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.billingType==1?'':'warning'">
              {{ scope.row.billingType==1?'CPM':'CPD' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="90" align="right">
          <template slot-scope="scope">
            <span style="color:#f56c6c;">￥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="曝光/点击" width="180">
          <template slot-scope="scope">
            <div style="display:flex; align-items:center; margin-bottom:4px;">
              <el-progress :percentage="getViewPercent(scope.row)" :stroke-width="6" style="flex:1;"></el-progress>
            </div>
            <div style="font-size:12px; color:#909399;">
              曝光:{{ scope.row.currentViews || 0 }} | 点击:{{ scope.row.clickCount || 0 }}
              <span v-if="scope.row.currentViews > 0" style="color:#67C23A;">
                ({{ getClickRate(scope.row) }}%)
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="投放时间" width="170">
          <template slot-scope="scope">
            {{ scope.row.startTime }} ~ {{ scope.row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="130" align="center">
          <template slot-scope="scope">
            <el-tag :type="getEffectiveTagType(scope.row.effectiveStatus)" size="mini" style="margin-bottom:4px;">
              {{ getEffectiveLabel(scope.row.effectiveStatus) }}
            </el-tag>
            <div>
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                active-text="投放"
                inactive-text="下线"
                @change="val=>handleToggleStatus(scope.row, val)">
              </el-switch>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="openEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" @click="doIncrementView(scope.row)">曝光</el-button>
            <el-button type="text" size="mini" style="color:#409EFF;" @click="doIncrementClick(scope.row)">点击</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
      <el-form :model="advertForm" label-width="110px" ref="advertForm">
        <el-form-item label="广告名称" required>
          <el-input v-model="advertForm.advertName" placeholder="请输入广告名称"></el-input>
        </el-form-item>
        <el-form-item label="广告形式">
          <el-radio-group v-model="advertForm.advertType">
            <el-radio :label="1">图片</el-radio>
            <el-radio :label="2">视频</el-radio>
            <el-radio :label="3">GIF动画</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="投放位置">
          <el-select v-model="advertForm.position" placeholder="请选择投放位置" style="width:100%;">
            <el-option label="首页轮播" value="首页轮播"></el-option>
            <el-option label="分类页" value="分类页"></el-option>
            <el-option label="商品详情页" value="商品详情页"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="广告图片">
          <el-upload
            class="advert-uploader"
            action="/mall-sys/Advert/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload">
            <img v-if="advertForm.imageUrl" :src="getImageUrl(advertForm.imageUrl)" class="advert-image">
            <i v-else class="el-icon-plus advert-uploader-icon"></i>
          </el-upload>
          <div class="el-upload__tip" style="color:#999; font-size:12px;">支持 jpg/png/gif，不超过 2MB</div>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="advertForm.linkUrl" placeholder="请输入跳转链接"></el-input>
        </el-form-item>
        <el-form-item label="计费方式">
          <el-radio-group v-model="advertForm.billingType">
            <el-radio :label="1">CPM（按展示付费）</el-radio>
            <el-radio :label="2">CPD（按天付费）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="advertForm.price" :min="0" :precision="2" :step="10" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="售卖量">
          <el-input-number v-model="advertForm.totalViews" :min="0" :step="1000" style="width:100%;"></el-input-number>
          <div style="color:#999; font-size:12px; margin-top:4px;">总展现量，0表示不限</div>
        </el-form-item>
        <el-form-item label="投放时间">
          <el-date-picker
            v-model="timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width:100%;">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否首刷">
          <el-switch v-model="advertForm.isFirst" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
        <el-form-item label="投放权重">
          <el-input-number v-model="advertForm.weight" :min="1" :max="10" :step="1" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="advertForm.status" :active-value="1" :inactive-value="0" active-text="投放中" inactive-text="已下线"></el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="doSave">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  list,
  addAdvert,
  updateAdvert,
  deleteAdvert,
  batchDelete,
  toggleStatus,
  refreshExpired,
  incrementView,
  incrementClick
} from '@/api/pms_advert.js'

export default {
  name: 'AdvertManage',
  data() {
    return {
      /* 搜索 */
      searchForm: {
        advertName: '',
        position: '',
        status: null
      },

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
      advertForm: {},
      timeRange: []
    }
  },
  methods: {
    /* 上传前校验 */
    beforeUpload(file) {
      var isImage = file.type.indexOf('image/') === 0
      var isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) { this.$message.error('只能上传图片文件'); return false }
      if (!isLt2M) { this.$message.error('图片大小不能超过 2MB'); return false }
      return true
    },

    /* 上传成功回调 */
    handleUploadSuccess(response) {
      if (response.code === 200 || response.code === 0) {
        this.advertForm.imageUrl = response.fileName
        this.$message.success('上传成功')
      } else {
        this.$message.error('上传失败')
      }
    },

    /* 拼接图片展示地址 */
    getImageUrl(fileName) {
      if (!fileName) return ''
      if (fileName.indexOf('http') === 0) return fileName
      return '/mall-sys/Advert/showImg/' + fileName
    },

    /* 搜索 */
    doSearch() {
      this.curPage = 1
      this.loadList()
    },

    /* 重置 */
    doReset() {
      this.searchForm = {
        advertName: '',
        position: '',
        status: null
      }
      this.curPage = 1
      this.loadList()
    },

    /* 加载列表 */
    loadList() {
      var params = {
        advertName: this.searchForm.advertName || null,
        position: this.searchForm.position || null,
        status: this.searchForm.status
      }
      list(this.curPage, this.pageSize, params).then(resp => {
        var data = resp.data || []
        data.forEach(function(item) {
          item.status = Number(item.status)
          item.advertType = Number(item.advertType)
          item.billingType = Number(item.billingType)
          item.isFirst = Number(item.isFirst)
          item.clickCount = Number(item.clickCount || 0)
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

    /* 广告形式 */
    getTypeLabel(type) {
      if (type == 1) return '图片'
      if (type == 2) return '视频'
      if (type == 3) return 'GIF'
      return '未知'
    },
    getTypeTag(type) {
      if (type == 1) return ''
      if (type == 2) return 'warning'
      if (type == 3) return 'success'
      return 'info'
    },

    /* 展现量百分比 */
    getViewPercent(row) {
      if (!row.totalViews || row.totalViews == 0) return 0
      return Math.min(100, Math.round((row.currentViews / row.totalViews) * 100))
    },

    /* 上下线切换 */
    handleToggleStatus(row, val) {
      toggleStatus(row.id, val).then(() => {
        this.$message.success(val == 1 ? '已上线' : '已下线')
      }).catch(() => {
        row.status = val == 1 ? 0 : 1
      })
    },

    /* 打开新增 */
    openAdd() {
      this.opType = 'add'
      this.dialogTitle = '添加广告'
      this.advertForm = {
        advertName: '',
        advertType: 1,
        position: '首页轮播',
        imageUrl: '',
        linkUrl: '',
        billingType: 1,
        price: 0,
        totalViews: 10000,
        weight: 1,
        isFirst: 0,
        status: 1
      }
      this.timeRange = []
      this.dialogVisible = true
    },

    /* 打开编辑 */
    openEdit(row) {
      this.opType = 'edit'
      this.dialogTitle = '编辑广告【' + row.advertName + '】'
      this.advertForm = {
        id: row.id,
        advertName: row.advertName,
        advertType: row.advertType,
        position: row.position,
        imageUrl: row.imageUrl,
        linkUrl: row.linkUrl,
        billingType: row.billingType,
        price: row.price,
        totalViews: row.totalViews,
        currentViews: row.currentViews,
        weight: row.weight,
        isFirst: row.isFirst,
        status: row.status
      }
      if (row.startTime && row.endTime) {
        this.timeRange = [row.startTime, row.endTime]
      } else {
        this.timeRange = []
      }
      this.dialogVisible = true
    },

    /* 保存 */
    doSave() {
      if (!this.advertForm.advertName || !this.advertForm.advertName.trim()) {
        this.$message.warning('请输入广告名称')
        return
      }
      if (this.timeRange && this.timeRange.length == 2) {
        this.advertForm.startTime = this.timeRange[0]
        this.advertForm.endTime = this.timeRange[1]
      }
      if (this.opType == 'add') {
        addAdvert(this.advertForm).then(() => {
          this.$message.success('添加成功')
          this.dialogVisible = false
          this.loadList()
        })
      } else {
        updateAdvert(this.advertForm).then(() => {
          this.$message.success('更新成功')
          this.dialogVisible = false
          this.loadList()
        })
      }
    },

    /* 单个删除 */
    handleDelete(row) {
      this.$confirm('确认删除广告【' + row.advertName + '】？', '提示', {
        type: 'warning'
      }).then(() => {
        deleteAdvert(row.id).then(() => {
          this.$message.success('删除成功')
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 批量删除 */
    handleBatchDelete() {
      var ids = this.multipleSelection.map(function(item) { return item.id })
      this.$confirm('确认删除选中的 ' + ids.length + ' 条广告？', '警告', {
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

    /* 有效状态标签 */
    getEffectiveLabel(status) {
      if (status == 0) return '已下线'
      if (status == 1) return '投放中'
      if (status == 2) return '未开始'
      if (status == 3) return '已过期'
      return '未知'
    },
    getEffectiveTagType(status) {
      if (status == 0) return 'info'
      if (status == 1) return 'success'
      if (status == 2) return 'warning'
      if (status == 3) return 'danger'
      return ''
    },

    /* 点击率 */
    getClickRate(row) {
      if (!row.currentViews || row.currentViews == 0) return 0
      return ((row.clickCount || 0) / row.currentViews * 100).toFixed(1)
    },

    /* 刷新过期状态 */
    doRefreshExpired() {
      this.$confirm('将所有已过期且仍在投放中的广告批量下线，确认执行？', '提示', {
        type: 'warning'
      }).then(() => {
        refreshExpired().then(resp => {
          this.$message.success('已刷新，下线 ' + (resp.count || 0) + ' 条过期广告')
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 模拟曝光 */
    doIncrementView(row) {
      incrementView(row.id).then(() => {
        row.currentViews = (row.currentViews || 0) + 1
      })
    },

    /* 模拟点击 */
    doIncrementClick(row) {
      incrementClick(row.id).then(() => {
        row.clickCount = (row.clickCount || 0) + 1
      })
    }
  },
  created() {
    this.loadList()
  }
}
</script>

<style scoped>
.box-card { margin: 12px; }
.advert-uploader >>> .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.advert-uploader >>> .el-upload:hover { border-color: #409EFF; }
.advert-uploader-icon { font-size: 28px; color: #8c939d; }
.advert-image { width: 120px; height: 120px; object-fit: cover; border-radius: 6px; }
</style>
