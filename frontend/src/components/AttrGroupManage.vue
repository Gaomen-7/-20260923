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

      <!-- 右侧：分组列表 -->
      <el-col :span="18">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>当前显示类别：<b style="color:#409EFF">{{ curCategoryName }}</b></span>
          </div>

          <!-- 操作栏 -->
          <div style="margin-bottom:12px;">
            <el-button type="success" @click="openAdd" :disabled="!curCategoryId">新增分组</el-button>
          </div>

          <!-- 表格 -->
          <el-table :data="tableData" style="width:100%" border>
            <el-table-column prop="id" label="编号" width="70"></el-table-column>
            <el-table-column prop="groupName" label="分组名称" width="140"></el-table-column>
            <el-table-column prop="descript" label="描述" min-width="160">
              <template slot-scope="scope">
                <span>{{ scope.row.descript || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="icons" label="图标" width="120">
              <template slot-scope="scope">
                <span>{{ scope.row.icons || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序" width="80"></el-table-column>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-form :model="groupForm" label-width="100px" ref="groupForm">
        <el-form-item label="分组名称" prop="groupName" required>
          <el-input v-model="groupForm.groupName" placeholder="请输入分组名称"></el-input>
        </el-form-item>
        <el-form-item label="所属分类">
          <el-input :value="curCategoryName" disabled></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="groupForm.descript" type="textarea" :rows="2" placeholder="请输入分组描述"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="groupForm.icons" placeholder="请输入图标标识"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="groupForm.sort" :min="0" :max="999"></el-input-number>
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
  addAttrGroup,
  updateAttrGroup,
  deleteAttrGroup
} from '@/api/pms_attrGroup.js'
import { getCategoryList } from '@/api/pms_category.js'

export default {
  name: 'AttrGroupManage',
  data() {
    return {
      /* 类别树 */
      categoryList: [],
      defaultProps: { children: 'children', label: 'categoryName' },
      curCategoryId: null,
      curCategoryName: '',

      /* 表格 */
      tableData: [],
      totalCount: 0,
      curPage: 1,
      pageSize: 10,

      /* 对话框 */
      dialogVisible: false,
      dialogTitle: '',
      opType: '',
      groupForm: {}
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
      list(this.curPage, this.pageSize, this.curCategoryId).then(resp => {
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
      this.dialogTitle = '添加属性分组【所属类别：' + this.curCategoryName + '】'
      this.groupForm = {
        groupName: '',
        categoryId: this.curCategoryId,
        descript: '',
        icons: '',
        sort: 0
      }
      this.dialogVisible = true
    },

    /* 打开编辑 */
    openEdit(row) {
      this.opType = 'edit'
      this.dialogTitle = '编辑属性分组【' + row.groupName + '】'
      this.groupForm = {
        id: row.id,
        groupName: row.groupName,
        categoryId: this.curCategoryId,
        descript: row.descript || '',
        icons: row.icons || '',
        sort: row.sort || 0
      }
      this.dialogVisible = true
    },

    /* 保存 */
    doSave() {
      if (!this.groupForm.groupName) {
        this.$message.warning('请输入分组名称')
        return
      }
      if (this.opType == 'add') {
        addAttrGroup(this.groupForm).then(() => {
          this.$message.success('添加成功')
          this.dialogVisible = false
          this.loadList()
        })
      } else {
        updateAttrGroup(this.groupForm).then(() => {
          this.$message.success('更新成功')
          this.dialogVisible = false
          this.loadList()
        })
      }
    },

    /* 删除 */
    doDelete(row) {
      this.$confirm('确认删除分组【' + row.groupName + '】吗？', '提示', {
        type: 'warning'
      }).then(() => {
        deleteAttrGroup(row.id).then(() => {
          this.$message.success('删除成功')
          this.loadList()
        })
      }).catch(() => {})
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
