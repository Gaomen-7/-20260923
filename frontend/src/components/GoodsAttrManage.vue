<template>
  <div>
    <el-row :gutter="16">
      <!-- 左侧：类别树 -->
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>请选择分类</span>
          </div>
          <el-tree
            :data="categoryList"
            :props="defaultProps"
            accordion
            node-key="id"
            highlight-current
            @node-click="handleNodeClick">
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧：属性列表 -->
      <el-col :span="18">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>当前显示类别：<b style="color:#409EFF">{{ curCategoryName }}</b></span>
          </div>

          <!-- 搜索区 -->
          <el-form :inline="true" :model="searchForm">
            <el-form-item label="属性名称">
              <el-input v-model="searchForm.attrName" placeholder="属性名称" style="width:160px;" clearable></el-input>
            </el-form-item>
            <el-form-item label="所属分组">
              <el-select v-model="searchForm.attrGroupId" placeholder="请选择" style="width:140px;" clearable>
                <el-option v-for="g in groupList" :key="g.id" :label="g.groupName" :value="g.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="doSearch">查询</el-button>
              <el-button type="success" @click="openAdd" :disabled="!curCategoryId">新增属性</el-button>
            </el-form-item>
          </el-form>

          <!-- 表格 -->
          <el-table :data="tableData" style="width:100%" border>
            <el-table-column prop="id" label="编号" width="70"></el-table-column>
            <el-table-column prop="attrName" label="属性名称" width="140"></el-table-column>
            <el-table-column prop="attrTypeName" label="属性类型" width="100"></el-table-column>
            <el-table-column prop="groupName" label="所属分组" width="120">
              <template slot-scope="scope">
                <span>{{ scope.row.groupName || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="valueTypeName" label="值类型" width="90"></el-table-column>
            <el-table-column prop="attrValue" label="可选值" min-width="160">
              <template slot-scope="scope">
                <span v-if="scope.row.valueType==2">
                  <el-tag v-for="(v,i) in splitValues(scope.row.attrValue)" :key="i" size="mini" style="margin-right:4px;">{{ v }}</el-tag>
                </span>
                <span v-else>{{ scope.row.attrValue }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template slot-scope="scope">
                <el-tag :type="scope.row.enable==1?'success':'info'" size="mini">
                  {{ scope.row.enable==1?'启用':'禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="openEdit(scope.row)">编辑</el-button>
                <el-button type="text" size="mini" style="color:#f56c6c;" @click="doDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <el-pagination
            background
            layout="total, prev, pager, next"
            :total="totalCount"
            :current-page="curPage"
            :page-size="pageSize"
            @current-change="handlePageChange"
            style="margin-top:12px; text-align:right;">
          </el-pagination>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px" :close-on-click-modal="false">
      <el-form :model="attrForm" label-width="100px" ref="attrForm">
        <el-form-item label="属性名称" prop="attrName" required>
          <el-input v-model="attrForm.attrName" placeholder="请输入属性名称"></el-input>
        </el-form-item>
        <el-form-item label="属性类型">
          <el-select v-model="attrForm.attrType" disabled style="width:100%;">
            <el-option label="规格参数" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="值类型">
          <el-select v-model="attrForm.valueType" placeholder="请选择" style="width:100%;" @change="onValueTypeChange">
            <el-option label="单值" :value="1"></el-option>
            <el-option label="多值" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="可选值">
          <!-- 单值：输入框 -->
          <el-input v-if="attrForm.valueType==1" v-model="attrForm.attrValue" placeholder="请输入可选值"></el-input>
          <!-- 多值：标签选择（可创建） -->
          <el-select v-else v-model="multiValues" multiple filterable allow-create
            default-first-option placeholder="输入后回车添加" style="width:100%;">
          </el-select>
        </el-form-item>
        <el-form-item label="所属分组">
          <el-select v-model="attrForm.attrGroupId" placeholder="请选择" style="width:100%;" clearable>
            <el-option v-for="g in groupList" :key="g.id" :label="g.groupName" :value="g.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属类别">
          <el-input :value="curCategoryName" disabled></el-input>
        </el-form-item>
        <el-form-item label="可否检索">
          <el-switch v-model="attrForm.searchEnable" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="attrForm.enable" :active-value="1" :inactive-value="0"></el-switch>
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
  addAttr,
  updateAttr,
  deleteAttr,
  groupOptions
} from '@/api/pms_goodsAttr.js'
import { getCategoryList } from '@/api/pms_category.js'

export default {
  name: 'GoodsAttrManage',
  data() {
    return {
      /* 类别树 */
      categoryList: [],
      defaultProps: { children: 'children', label: 'categoryName' },
      curCategoryId: null,
      curCategoryName: '',

      /* 分组下拉 */
      groupList: [],

      /* 搜索 */
      searchForm: {
        attrName: '',
        categoryId: null,
        attrType: 1,
        attrGroupId: null
      },

      /* 表格 */
      tableData: [],
      totalCount: 0,
      curPage: 1,
      pageSize: 10,

      /* 对话框 */
      dialogVisible: false,
      dialogTitle: '',
      opType: '',
      attrForm: {},
      multiValues: []
    }
  },
  methods: {
    /* 加载类别树 */
    loadCategoryTree() {
      getCategoryList().then(resp => {
        this.categoryList = resp.data || []
      })
    },

    /* 点击类别节点 */
    handleNodeClick(data) {
      this.curCategoryId = data.id
      this.curCategoryName = data.categoryName
      this.searchForm.categoryId = data.id
      this.searchForm.attrGroupId = null
      this.curPage = 1
      this.loadGroupOptions(data.id)
      this.loadList()
    },

    /* 加载分组下拉 */
    loadGroupOptions(categoryId) {
      groupOptions(categoryId).then(resp => {
        this.groupList = resp.data || []
      })
    },

    /* 查询 */
    doSearch() {
      this.curPage = 1
      this.loadList()
    },

    /* 加载列表 */
    loadList() {
      if (!this.curCategoryId) {
        this.tableData = []
        this.totalCount = 0
        return
      }
      var params = {
        attrName: this.searchForm.attrName || null,
        categoryId: this.curCategoryId,
        attrType: 1,
        attrGroupId: this.searchForm.attrGroupId || null
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

    /* 打开新增 */
    openAdd() {
      this.opType = 'add'
      this.dialogTitle = '添加规格参数【所属类别：' + this.curCategoryName + '】'
      this.attrForm = {
        attrName: '',
        attrType: 1,
        valueType: null,
        attrValue: '',
        attrGroupId: null,
        categoryId: this.curCategoryId,
        searchEnable: 0,
        enable: 1
      }
      this.multiValues = []
      this.dialogVisible = true
    },

    /* 打开编辑 */
    openEdit(row) {
      this.opType = 'edit'
      this.dialogTitle = '编辑规格参数【' + row.attrName + '】'
      this.attrForm = {
        id: row.id,
        attrName: row.attrName,
        attrType: 1,
        valueType: row.valueType,
        attrValue: row.attrValue || '',
        attrGroupId: row.attrGroupId || null,
        categoryId: this.curCategoryId,
        searchEnable: row.searchEnable || 0,
        enable: row.enable
      }
      if (row.valueType == 2 && row.attrValue) {
        this.multiValues = row.attrValue.split(';')
      } else {
        this.multiValues = []
      }
      this.dialogVisible = true
    },

    /* 值类型切换 */
    onValueTypeChange() {
      this.attrForm.attrValue = ''
      this.multiValues = []
    },

    /* 保存 */
    doSave() {
      if (!this.attrForm.attrName) {
        this.$message.warning('请输入属性名称')
        return
      }
      if (!this.attrForm.valueType) {
        this.$message.warning('请选择值类型')
        return
      }
      /* 多值时把标签数组转成分号分隔字符串 */
      if (this.attrForm.valueType == 2) {
        this.attrForm.attrValue = this.multiValues.join(';')
      }
      if (this.opType == 'add') {
        addAttr(this.attrForm).then(() => {
          this.$message.success('添加成功')
          this.dialogVisible = false
          this.loadList()
        })
      } else {
        updateAttr(this.attrForm).then(() => {
          this.$message.success('更新成功')
          this.dialogVisible = false
          this.loadList()
        })
      }
    },

    /* 删除 */
    doDelete(row) {
      this.$confirm('确认删除属性【' + row.attrName + '】吗？', '提示', {
        type: 'warning'
      }).then(() => {
        deleteAttr(row.id).then(() => {
          this.$message.success('删除成功')
          this.loadList()
        })
      }).catch(() => {})
    },

    /* 可选值拆分（多值显示用） */
    splitValues(val) {
      if (!val) return []
      return val.split(';')
    }
  },
  created() {
    this.loadCategoryTree()
  }
}
</script>

<style scoped>
.box-card { margin-bottom: 12px; }
</style>
