<template>
  <div>
	<el-card class="box-card">
	  <div slot="header" class="clearfix">
		<span>部门管理</span>
		<el-button style="float: right; padding: 3px 0" type="text" @click="reloadList">刷新</el-button>
	  </div>

	  <!-- 搜索过滤区 -->
	  <el-form :inline="true" :model="filterForm">
		<el-form-item label="部门名称">
		  <el-input v-model="filterForm.deptName" placeholder="请输入部门名称" clearable></el-input>
		</el-form-item>
		<el-form-item>
		  <el-button type="primary" @click="onFilter">查询</el-button>
		</el-form-item>
	  </el-form>

	  <!-- 部门树形列表 -->
	  <el-tree
		:data="deptList"
		:props="defaultProps"
		accordion
		node-key="id"
		default-expand-all>
		<span class="custom-tree-node" slot-scope="{ node, data }">
			<span>{{ data.deptName }}</span>
			<span style="float:right; margin-right:10px;">
				<el-button type="text" size="mini" @click.stop="handleAppend(data)">添加子部门</el-button>
				<el-button type="text" size="mini" style="color:#f56c6c;" @click.stop="handleDelete(data)">删除</el-button>
			</span>
		</span>
	  </el-tree>
	</el-card>

	<!-- 添加/编辑部门对话框 -->
	<el-dialog :title="dialogTitle" :visible.sync="showFormBox" width="40%"
			:before-close="handleClose" :close-on-click-modal="false">
		<el-form :model="deptForm" label-width="80px">
			<el-form-item label="部门名称">
				<el-input v-model="deptForm.deptName" placeholder="请输入部门名称"></el-input>
			</el-form-item>
		</el-form>
		<span slot="footer" class="dialog-footer">
			<el-button @click="showFormBox=false">取消</el-button>
			<el-button type="primary" @click="saveDept">{{ btnText }}</el-button>
		</span>
	</el-dialog>
  </div>
</template>

<script>
import { pickForm } from '@/utils/common.js'
import {
	getDeptList,
	addDept,
	updateDept,
	deleteDept
} from '@/api/pms_dept.js'

export default {
  name: 'Dept',

  /* {1} 数据区 */
  data () {
    return {
		deptList: [],
		defaultProps: {
			children: "children",
			label: "deptName"
		},
		/* 过滤搜索表单 */
		filterForm: {
			deptName: ""
		},
		/* 部门表单 */
		deptForm: {
			deptName: "",
			parentId: null
		},
		opMode: "",          /* 操作模式: add/edit */
		dialogTitle: "",      /* 对话框标题 */
		btnText: "",          /* 按钮文字 */
		showFormBox: false    /* 对话框显示开关 */
	}
  },

  /* {2}方法区【START】 */
  methods:{
	/* 【M1】setLeap()【TODO】
	功能解析: 在每一个节点上标记它是否为叶子节点。*/
	setLeap(childList, level){
		childList.forEach(
			(D)=>{
				D.level = level;
				console.log("【遍历节点】【%s】【%s】",
					D.categoryName, D.level );
				let children = D.children;
				if( !children || children.length==0 ){
					D.isLeap = true;
				}else{
					D.isLeap = false;
					this.setLeap(children, level+1);
				}
			}
		);
	},

	/* 【M2】重载部门列表【TODO】 */
	reloadList(){
		getDeptList()
		.then(
			resp=>{
				this.deptList = resp.data;
				this.setLeap(this.deptList, 1);
			}
		);
	},

	/* 【M4】过滤部门名称。【TODO】 */
	onFilter(){
		let keyword = this.filterForm.deptName;
		if( keyword && keyword!="" ){
			let retList = [];   /* 结果列表。 */
			/* 1.调用下面的过滤方法来检索部门. */
			this.doFilter( this.deptList, keyword, retList );
			this.deptList = retList;
		}else{
			/* 2.从后台重新下载列表. */
			this.reloadList();
		}
	},

	/* 【M5】递归过滤部门名称。 */
	doFilter( list, keyword, retList ){
		if( !list || list.length==0 ) return;
		list.forEach( (item)=>{
			if( item.deptName && item.deptName.indexOf(keyword) >= 0 ){
				retList.push(item);
			}
			if( item.children && item.children.length > 0 ){
				this.doFilter(item.children, keyword, retList);
			}
		});
	},

	/* 【M6】显示添加部门的对话框。【TODO】 */
	handleAppend( parDept ){
		let parName = parDept.deptName;
		this.deptForm = {
			parentId: parDept.id
		};
		this.opMode = 'add';
		this.dialogTitle = `添加【${parName}】的子级部门`;
		this.btnText = "保存添加";
		this.showFormBox = true;
	},

	/* 【M7】添加部门。【TODO】 */
	doAddDept(){
		let deptName = this.deptForm.deptName;
		/* 1.调用 API 方法保存数据到后台。*/
		addDept( this.deptForm )
		.then(
			resp=>{
				this.$message(`添加【${deptName}】部门成功。`);
				this.opMode = "";
				/* 2.清空检索的信息 */
				this.clearSearch();
				/* 3.重新加载树形列表. */
				this.reloadList();
			}
		);
	},

	/* 【M8】更新部门。【TODO】 */
	doUpdateDept(){
		let deptName = this.deptForm.deptName;
		/* 1.调用 API 方法保存数据到后台。*/
		updateDept( this.deptForm )
		.then(
			resp=>{
				this.$message(`更新【${deptName}】部门成功。`);
				this.opMode = "";
				/* 2.清空检索的信息 */
				this.clearSearch();
				/* 3.重新加载树形列表. */
				this.reloadList();
			}
		);
	},

	/* 【M9】保存部门。【TODO】 */
	saveDept(){
		/* 【ps】当点击保存部门时, 触发以下逻辑。*/
		if( this.opMode=="add" ){
			this.doAddDept();
		}else{
			this.doUpdateDept();
		}
	},

	/* 【M10】删除部门。【TODO】 */
	handleDelete( dept ){
		let id = dept.id;
		let deptName = dept.deptName;
		this.$confirm(`你确认要删除【${deptName}】节点吗?`,
			"安全警告",{
			type:"warning"
		}).then(()=>{
			/* 1.点击确认会执行以下逻辑. */
			this.confirmDelete(id, deptName);
		}).catch(()=>{});
	},

	/* 确认删除逻辑 */
	confirmDelete(id, deptName){
		deleteDept(id)
		.then(
			resp=>{
				this.$message(`删除【${deptName}】部门成功。`);
				this.clearSearch();
				this.reloadList();
			}
		);
	},

	/* 清空检索信息 */
	clearSearch(){
		this.filterForm.deptName = "";
		this.showFormBox = false;
	},

	/* 关闭对话框 */
	handleClose(){
		this.showFormBox = false;
	}
  },

  /* {3}生命周期方法 */
  created(){
	 this.reloadList();
  }

}
</script>

<style>
.custom-tree-node{
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: space-between;
	font-size: 16px;
	padding-right: 8px;
}
</style>
