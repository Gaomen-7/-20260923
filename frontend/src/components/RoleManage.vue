<template>
  <div>
    <el-card class="box-card">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="角色名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button type="success" @click="doShowAdd">添加角色</el-button>
        </el-form-item>
      </el-form>

      <!-- 角色表格 -->
      <el-table :data="roleList" border style="width:100%">
        <el-table-column prop="id" label="编号" width="70"></el-table-column>
        <el-table-column prop="roleName" label="角色名称" width="140"></el-table-column>
        <el-table-column prop="descript" label="描述" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createDate" label="创建时间" width="160"></el-table-column>
        <el-table-column label="操作" width="280">
          <template slot-scope="scope">
            <el-button size="mini" @click="doShowPerm(scope.row)" :disabled="scope.row.id===1">分配权限</el-button>
            <el-button size="mini" @click="doShowEdit(scope.row)" :disabled="scope.row.id===1">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)" :disabled="scope.row.id===1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination background layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount" :current-page="curPage" :page-size="pageSize"
        @current-change="reloadPage" @size-change="onSizeChange"
        style="margin-top:10px;"></el-pagination>
    </el-card>

    <!-- 添加/编辑角色对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="showFormBox" width="40%"
      :close-on-click-modal="false">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="角色名称" required>
          <el-input v-model="roleForm.roleName" placeholder="如：商品管理员"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="roleForm.descript" placeholder="角色职责说明"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showFormBox=false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </span>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog title="分配权限" :visible.sync="showPermBox" width="50%"
      :close-on-click-modal="false">
      <div style="margin-bottom:10px;">
        <el-button size="mini" @click="checkAll">全选</el-button>
        <el-button size="mini" @click="checkNone">全不选</el-button>
        <span style="margin-left:10px;color:#888;">当前角色：{{ currentRoleName }}</span>
      </div>
      <el-tree ref="permTree" :data="permTreeData" show-checkbox
        node-key="id" :props="{label:'label', children:'children'}"
        default-expand-all></el-tree>
      <span slot="footer">
        <el-button @click="showPermBox=false">取消</el-button>
        <el-button type="primary" @click="doAssignPerm">确认分配</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  listRole, addRole, updateRole, deleteRole,
  getRole, permissionList, assignPermissions
} from '@/api/pms_role.js'

export default {
  name: 'RoleManage',
  data() {
    return {
      searchForm: { roleName: '' },
      roleList: [],
      curPage: 1,
      pageSize: 10,
      totalCount: 0,
      // 添加/编辑
      showFormBox: false,
      dialogTitle: '',
      opMode: 'add',
      roleForm: {},
      // 权限分配
      showPermBox: false,
      currentRoleId: null,
      currentRoleName: '',
      permTreeData: []
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    loadList() {
      let param = {}
      if (this.searchForm.roleName) {
        param.roleName = this.searchForm.roleName
      }
      listRole(this.curPage, this.pageSize, param).then(resp => {
        this.roleList = resp.data
        this.totalCount = resp.total
      })
    },
    onSearch() {
      this.curPage = 1
      this.loadList()
    },
    reloadPage(p) {
      this.curPage = p
      this.loadList()
    },
    onSizeChange(s) {
      this.pageSize = s
      this.curPage = 1
      this.loadList()
    },
    doShowAdd() {
      this.opMode = 'add'
      this.roleForm = {}
      this.dialogTitle = '添加角色'
      this.showFormBox = true
    },
    doShowEdit(row) {
      this.opMode = 'edit'
      this.roleForm = { id: row.id, roleName: row.roleName, descript: row.descript }
      this.dialogTitle = '编辑角色'
      this.showFormBox = true
    },
    doSave() {
      if (!this.roleForm.roleName) {
        this.$message('请输入角色名称')
        return
      }
      let fn = this.opMode === 'add' ? addRole(this.roleForm) : updateRole(this.roleForm)
      fn.then(() => {
        this.$message(this.opMode === 'add' ? '添加成功' : '更新成功')
        this.showFormBox = false
        this.loadList()
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除角色【' + row.roleName + '】？', '警告', { type: 'warning' })
        .then(() => {
          deleteRole(row.id).then(() => {
            this.$message('删除成功')
            this.loadList()
          })
        }).catch(() => {})
    },
    // 权限分配
    doShowPerm(row) {
      this.currentRoleId = row.id
      this.currentRoleName = row.roleName
      // 并行加载权限树 + 角色已有权限
      permissionList().then(permResp => {
        // 把平铺权限点按模块分组（permission 格式：模块:操作）
        let allPerms = permResp.data || []
        let moduleMap = {}
        allPerms.forEach(p => {
          let parts = p.permission.split(':')
          let mod = parts[0]
          if (!moduleMap[mod]) {
            moduleMap[mod] = []
          }
          moduleMap[mod].push({ id: p.id, label: p.permission })
        })
        this.permTreeData = Object.keys(moduleMap).map((mod, idx) => ({
          id: 'm_' + idx,
          label: this.moduleLabel(mod),
          children: moduleMap[mod]
        }))
        // 加载角色已选权限
        getRole(row.id).then(roleResp => {
          let checkedIds = (roleResp.data && roleResp.data.permissionIds) || []
          this.$nextTick(() => {
            this.$refs.permTree.setCheckedKeys(checkedIds)
          })
          this.showPermBox = true
        })
      })
    },
    moduleLabel(mod) {
      let map = {
        user: '用户管理', dept: '部门管理', role: '角色管理',
        goods: '商品管理', category: '商品分类', brand: '品牌管理', attr: '属性管理',
        order: '订单管理', inventory: '库存管理',
        coupon: '优惠券', member: '会员管理', advert: '广告管理',
        dashboard: '数据看板', analysis: '数据分析'
      }
      return map[mod] || mod
    },
    checkAll() {
      let allIds = []
      this.permTreeData.forEach(mod => {
        mod.children.forEach(c => allIds.push(c.id))
      })
      this.$refs.permTree.setCheckedKeys(allIds)
    },
    checkNone() {
      this.$refs.permTree.setCheckedKeys([])
    },
    doAssignPerm() {
      let leafNodes = this.$refs.permTree.getCheckedNodes(true, false)
      let permissionIds = leafNodes.map(n => n.id)
      assignPermissions(this.currentRoleId, permissionIds).then(() => {
        this.$message('权限分配成功')
        this.showPermBox = false
      })
    }
  }
}
</script>

<style scoped>
.box-card { margin: 0; }
</style>
