<template>
  <div>
	<!-- 【1】【el-tabs|页签】 -->
	<!-- 请覆盖我 -->

	<el-row :gutter="24">
	  <!-- 【2】【el-col|部门列表】 -->
	  <!-- 请覆盖我 -->

	  <el-col :span="19">
		<el-card class="box-card">
		
			<!-- 【3】【el-form|搜索框】【START】 -->
			<!-- 请覆盖我 -->
			<!-- 【3】【el-form|搜索框】【END】 -->
			
		
			<!-- 【4】【el-table|用户表格】【START】 -->

			<!-- 【4】【el-table|用户表格】【END】 -->


		<!-- 【5】【分页条】【START】 -->

		<!-- 【5】【分页条】【END】 -->

		</el-card>	  
	  </el-col>
	</el-row>

	<!-- 【6】【添加用户对话框】【START】  -->
	
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


		/* 2.搜索栏相关数据。*/

		
		/* 3.用户列表相关数据。*/
		
		
		/* 4.用户表单相关数据。 */

		
		
		/* 5.其它数据。*/
	}
  },  /* 【DATA-END】 */
  
  /*{2}方法区 */
  methods:{
	/* M01.重载用户列表.【TODO】 */


	/* M02.点击部门节点触发此方法。【TODO】 */


	/* M03.点击搜索触发此方法。【TODO】 */


	/* M04.显示添加对话框。【TODO】 */


	/* M05.添加用户。【TODO】 */


	/* M06.更新用户。【TODO】 */


	/* M07.保存用户 【TODO】 */


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
	
	/* M11.删除按钮触发事件.【TODO】 */
	

	
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
