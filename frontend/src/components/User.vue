<template>
  <div>
	<!-- 【1】【el-tabs|页签】【START】 -->
	<el-tabs type="border-card">
	  <el-tab-pane>
		<span slot="label"><i class="el-icon-date"></i>用户列表</span>
	  </el-tab-pane>
	  <el-tab-pane label="添加用户">添加用户</el-tab-pane>
	</el-tabs>
	<!-- 【1】【el-tabs|页签】【END】 -->

	<el-row :gutter="24">
	  <!-- 【2】【el-col|部门列表】【START】 -->
		<el-col :span="5">
			<el-card class="box-card">
			  <div slot="header" class="clearfix">
				<span>请选择部门</span>
				<el-button style="float: right; padding: 3px 0"
					type="text">操作按钮</el-button>
			  </div>
				<el-tree
				  :data="deptList"
				  :props="defaultProps"
				  accordion
				  @node-click="handleNodeClick">
				</el-tree>
			</el-card>
		</el-col>
	  <!-- 【2】【el-col|部门列表】【END】 -->

	  <el-col :span="19">
		<el-card class="box-card">

			<!-- 【3】【el-form|搜索框】【START】 -->
			<el-form :inline="true"
				:model="searchForm" class="demo-form-inline">
			  <el-form-item label="用户名称">
				<el-input v-model="searchForm.account"
				placeholder="用户名称"
				:disabled="searchForm.deptId===''">
				</el-input>
			  </el-form-item>

			  <el-form-item label="角色">
				<el-select v-model="searchForm.roleId"
					style="width:150px;"
					placeholder="角色"
					:disabled="searchForm.deptId===''">
				  <el-option label="请选择角色" value=""></el-option>
				  <el-option
					 v-for="(item,index) in roleList"
					 :key="index"
					 :label="item.label"
					 :value="item.value">
				  </el-option>
				</el-select>
			  </el-form-item>

			  <el-form-item label="性别">
				<el-select v-model="searchForm.sex"
					style="width:150px;"
					placeholder="请选择性别"
					:disabled="searchForm.deptId===''">
				  <el-option label="请选择性别" value=""></el-option>
				  <el-option label="男" value="男"></el-option>
				  <el-option label="女" value="女"></el-option>
				</el-select>
			  </el-form-item>

			  <el-form-item>
				<el-button type="primary"
					@click="onSearch"
					:disabled="searchForm.deptId===''">
					查询
				</el-button>
				<el-button type="primary"
					@click="doShowAddBox"
					:disabled="searchForm.deptId===''">
					添加
				</el-button>
				<el-button type="primary" @click="doBatchDelete">批量删除</el-button>
			  </el-form-item>
			</el-form>
			<!-- 【3】【el-form|搜索框】【END】 -->


			<!-- 【4】【el-table|用户表格】【START】 -->
			<el-table
					:data="userList"
					style="width: 100%">
					<el-table-column
					  prop="id"
					  label="编号"
					  width="80">
					</el-table-column>
					<el-table-column
					  prop="account"
					  label="帐号"
					  width="100">
					</el-table-column>
					<el-table-column
					  prop="nickName"
					  label="昵称"
					  width="100">
					</el-table-column>
					<el-table-column
					  prop="phone"
					  label="电话"
					  width="150">
					</el-table-column>
					<el-table-column
					  prop="sex"
					  label="性别"
					  width="80">
					</el-table-column>
					<el-table-column
					  prop="no"
					  label="工号"
					  width="80">
					</el-table-column>
					<el-table-column
					  prop="deptName"
					  label="部门"
					  width="80">
					</el-table-column>
					<el-table-column
					  prop="roleName"
					  label="角色"
					  width="80">
					</el-table-column>
					<el-table-column
					  prop="createDate"
					  label="创建日期"
					  width="120">
					</el-table-column>
					<el-table-column label="操作">
					  <template slot-scope="scope">
						<el-button
						  size="mini"
						  @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
						<el-button
						  size="mini"
						  type="danger"
						  @click="handleDelete(scope.$index, scope.row)">删除</el-button>
					  </template>
					</el-table-column>
				</el-table>
			<!-- 【4】【el-table|用户表格】【END】 -->


		<!-- 【5】【分页条】【START】 -->
		<el-pagination
			background
			layout="total, sizes, prev, pager, next, jumper"
			:total="totalCount"
			:current-page="curPage"
			@current-change="reloadPage"
			style="margin-top:5px;">
		</el-pagination>
		<!-- 【5】【分页条】【END】 -->

		</el-card>
	  </el-col>
	</el-row>

	<!-- 【6】【添加用户对话框】【START】  -->
	<el-dialog :title="dialogTitle" :visible.sync="showFormBox" width="60%"
			:before-close="handleClose" style="padding:8px 15px;"
			@close="onClose" :close-on-click-modal="false" >

			<!-- 5.1 [放置表单] -->
			<el-form :model="userForm" :rules="userFormRules"
				   ref="userFormRef" >
				<el-form-item label="用户帐号" prop="account"
						style="margin-top:5px;">
					<el-input v-model="userForm.account"
						:readonly="opMode=='edit'" ></el-input>
				</el-form-item>
				<el-form-item label="用户昵称" prop="nickName"
					style="margin-top:5px;">
					<el-input v-model="userForm.nickName"></el-input>
				</el-form-item>
				<el-form-item label="性别" prop="sex"
					style="margin-top:5px;">
					<el-select v-model="userForm.sex" placeholder="请选择性别">
					  <el-option label="男" value="男"></el-option>
					  <el-option label="女" value="女"></el-option>
					</el-select>
				</el-form-item>

				<el-form-item label="角色">
					<el-select v-model="userForm.roleId" placeholder="角色">
					    <el-option label="请选择角色" value=""></el-option>
							<el-option
							 v-for="(item,index) in roleList"
							 :key="index"
							 :label="item.label"
							 :value="item.value">
						  </el-option>
					</el-select>
				</el-form-item>

				<el-form-item label="工号" prop="no"
					style="margin-top:5px;">
					<el-input v-model="userForm.no"></el-input>
				</el-form-item>
				<el-form-item label="电邮" prop="email"
					style="margin-top:5px;">
					<el-input v-model="userForm.email"></el-input>
				</el-form-item>
				<el-form-item label="电话" prop="phone"
					style="margin-top:5px;">
					<el-input v-model="userForm.phone"></el-input>
				</el-form-item>
			</el-form>
			<!-- 5.2 [放置 span] -->
			<span slot="footer" class="dialog-footer">
				<el-button @click="showFormBox=false">取消</el-button>
				<el-button type="primary" @click="doSaveUser">确定</el-button>
			</span>
		</el-dialog>
	<!-- 【6】【添加用户对话框】【END】 -->


  </div>
</template>

<script>
import { pickForm } from '@/utils/common.js'
import { getDeptList } from '@/api/pms_dept.js'
import { roleOptions } from '@/api/pms_role.js'
import {
	getUserList,
	addUser,
	updateUser,
	deleteUser,
	/* doBatchDelete */
} from '@/api/pms_user.js'

export default {
  name: 'User',

  /*{1} 数据区 */
  data () {
    return {
		/* 1.部门列表相关数据. */
		deptList: [],           /* 部门树形列表 */
		/* 告诉 ElementUI 我的数据列表中, 使用什么属性名表示子部门。 */
		defaultProps: {
			children: "children",
			label: "deptName"
		},

		/* 2.搜索栏相关数据【搜索框绑定对象】。*/
		searchForm: {
			account:"",
			roleId:"",
			deptId:"",
			sex:"",
		},
		roleList: [],   /* 角色下拉列表 */

		/* 3.用户列表相关数据【表格】。*/
		userList: [],
		curPage: 1,        /* 当前页 */
		pageSize: 10,      /* 页大小 */
		totalCount: 0,     /* 总记录数 */

		/* 4.用户表单相关数据【添加修改使用】。*/
		userForm: { },
		dialogTitle: "添加用户",
		showFormBox: false,    /* 显示对话框开关. */
		opMode: "add",         /* 操作模式:添加/编辑. */

		/* 4.1.表单验证规则【对话框使用】。*/
		userFormRules: {
			account: [
				{required: true, message: "请输入用户帐号", trigger: "blur"},
				{min: 3, max: 10, message: "帐号长度要求: (3-10)字符"},
			],
			roleId: [
				{required: true, message: "请选择角色", trigger: "blur"},
			]
		},

		/* 5.其它数据。*/
	}
  },  /* 【DATA-END】 */

  /*{2}方法区 */
  methods:{
	/* M01.重载用户列表.【TODO】 */
	reloadList(param, page){
		/* 1.设置当前为第几页。 */
		this.curPage = page;
		/* 2.调用 API 获取用户列表。 */
		getUserList(this.curPage, this.pageSize, param)
		.then(resp=>{
			/* 2.1保存列表数据。 */
			this.userList = resp.data;
			this.totalCount = resp.total;
		})
		.catch(error=>{});
	},

	/* M02.点击部门节点触发此方法。【TODO】 */
	handleNodeClick(row, node, component){
		this.searchForm = {};
		/* 1.锁定死部门 ID.*/
		this.searchForm.deptId = row.id;
		/* 2.抓取搜索表单的关键字.*/
		let param = pickForm(this.searchForm);
		/* 3.重新加载用户列表.*/
		this.reloadList(param, 1);
	},

	/* M03.点击【搜索按钮】触发此方法。【TODO】 */
	onSearch(){
		/* 1.抓取搜索框中非空的数据。 */
		let param = pickForm(this.searchForm);
		/* 2.重新加载用户列表, 传搜索数据。 */
		this.reloadList(param, 1);
	},

	/* M04.点击【添加】==>显示【添加对话框】。【TODO】 */
	doShowAddBox(){
		if( !this.searchForm.deptId ){
			this.$message("【系统】请选择部门。");
			return;
		}
		this.userForm = {};       /* 置空表单绑定数据。 */
		this.opMode = "add";       /* 添加模式 */
		this.showFormBox = true;   /* 打开对话框显示开关 */
		this.dialogTitle = "添加用户";
	},

	/* M05.添加用户写入后台。【TODO】 */
	doAddUser(){
		/* 1.设置用户所属的部门。 */
		this.userForm['deptId'] = this.searchForm.deptId;
		let account = this.userForm.account;
		/* 2.调用 API 方法, 写入后台数据. */
		addUser( this.userForm )
		.then(
			resp=>{
				/* 当保存成功后回来重置搜索表单, 刷新列表. */
				this.showFormBox = false;
				this.searchForm.roleId = '';
				this.searchForm.account = '';
				this.$message(`添加【${account}】用户成功。`);
				let param = pickForm(this.searchForm);
				this.reloadList(param, 1);
			}
		);
	},

	/* M06.更新用户写入后台。【TODO】 */
	doUpdateUser(){
		/* 1.设置用户所属的部门。 */
		let account = this.userForm.account;
		/* 2.调用 API 方法, 写入后台数据. */
		updateUser( this.userForm )
		.then(
			resp=>{
				/* 当保存成功后回来重置搜索表单, 刷新列表. */
				this.showFormBox = false;
				this.searchForm.roleId = '';
				this.searchForm.account = '';
				this.$message(`更新【${account}】用户成功。`);
				let param = pickForm(this.searchForm);
				this.reloadList(param, 1);
			}
		);
	},

	/*
	M07.保存用户 【TODO】
	点击对话框【保存】==>【触发以下函数】
	*/
	doSaveUser(){
		/* 区分现在是 添加/更新 */
		if( this.opMode=="add" ){
			this.doAddUser();
		}else{
			this.doUpdateUser();
		}
	},

	/* M08.批量删除用户 【TODO】 */
	doBatchDelete(){

	},

	/* M09.分页按钮触发事件 【TODO】 */
	reloadPage( newPage ){
		let param = pickForm(this.searchForm);
		this.reloadList( param, newPage );
	},

	/* M10.编辑按钮触发事件 【TODO】 */
	handleEdit( index, user ){
		let roleId = parseInt( user.roleId );
		this.userForm = user;
		this.userForm.roleId = roleId;

		this.opMode = 'edit';    //编辑模式
		this.showFormBox = true; //显示对话框
		this.dialogTitle = "编辑用户";
	},

	/* M11.点击【删除】按钮触发事件.【TODO】 */
	handleDelete(index, row){
		let id = row.id;
		let account = row.account;
		/* 1.提示用户要删除数据了。*/
		this.$confirm(
			`确认要删除【${account}】用户吗?`,
			"安全警告",
			{type:"warning"}
		).then(()=>{
			/* 当点击了确认后, 以下删除。*/
			this.confirmDelete(id, account);
		}).catch(()=>{});
	},

	/* M12.确认删除逻辑.【--】 */
	confirmDelete(id, account){
		deleteUser(id)
		.then(
		  resp=>{
				this.$message("删除【"+ account +"】用户成功。");
				let param = pickForm( this.searchForm );
				this.reloadList( param, 1 );
		  }
		)
	},

	/* M13.处理关闭事件.【--】 */
	handleClose(){
		console.log("handleClose()..");
		this.showFormBox = false;
	},

	/* M14.关闭按钮触发. 【--】 */
	onClose(){
		console.log("onClose()..");
	},

	/* M15.加载部门列表. 【TODO】 */
	loadDeptTree(){
		/* 1.调用 api 从后台获取数据。*/
		getDeptList()
		.then(resp=>{
			/* 1.保存部门数据到VUE的某个数据项。*/
			this.deptList = resp.data;
			/* 2.获取角色下拉菜单数据。*/
			this.loadRoleOptions();
		});
	},

	/* M16.加载角色列表. 【--】 */
	loadRoleOptions(){
		roleOptions().then(
			resp=>{
				this.roleList = resp.data;
				console.log( resp.data );
			}
		);
	}

  }, /* [METHOD-END] */

  /*{3}生命周期方法*/
  created(){
	 this.loadDeptTree();
  }

} /* ★★VUE★★ [END] */
</script>

<style>
span.el-tree-node__label{
	font-size:17px;
}
</style>
