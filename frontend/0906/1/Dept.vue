<template>
  <div>

	<!-- 【1】【TABS|页签】【START】 -->
	<!-- 请覆盖掉我! -->
	<!-- 【1】【TABS|页签】【END】 -->
	
	<el-row :gutter="24">
	    <!-- 【2】【部门列表】【START】 -->
		<!-- 请覆盖掉我! -->
		<!-- 【2】【部门列表】【END】 -->
		
	  <el-col :span="18">			
		<el-card class="box-card" v-show="opMode=='edit' || opMode=='add'">		
			<!-- 【D03:START】【部门表单】 -->
			<!-- 请覆盖掉我! -->
			<!-- 【D03:END】【部门表单】 -->
		</el-card>	  
	  </el-col>
	</el-row>

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
  
  /*{1} 数据区 */
  data () {
    return {
	
		/* 1.部门列表相关数据. */
		deptList:[],
		defaultProps:{
			children: "children",
			label: "deptName"
		},

		/* 2.搜索栏相关数据. */
		filterForm:{
			id: "",
			deptName:""
		},
		
		/* 3.部门表单相关数据. */
		deptForm:{ },
		dialogTitle:'添加部门',
		showFormBox:false,
		opMode:'',       /* 添加模式|编辑模式 */
		deptRules:{      /* 表单验证规则 */
			deptName:[
				{required:true, message:'请输入部门', trigger:'blur'},
				{min:3, max:10, message:'部门长度要求:(3-10)', trigger:'blur'},
			],
		},
		btnText:"",
		/* 5.其它数据。*/
	}
  },  /* [DATA-END] */
  
  /*{2}方法区 */
  methods:{
    /* 【M1】setLeap()【TODO】*/

	/* 【M2】重载部门列表【TODO】*/

	/* 【M3】点击部门节点触发此方法。【---】*/
	handleNodeClick( row, node, component ){
		console.log("[DEBUG]你点击了部门:【%s】", row.id);
		this.deptForm = row;
		this.opMode = 'edit';    /* 编辑模式 */
		this.dialogTitle = `编辑部门【${row.id}】`;
		this.btnText = "保存编辑";
	},

	/* 【M4】过滤部门名称。【TODO】*/
	
	/* 【M5】过滤部门名称。【--】*/
	doFilter( list, keyword, retList ){
		let find = 0;		
		for(let i=0; i<list.length; i++){
			let DP = list[i];
			let nDP = this.copyDP( DP );
			/* a.处理非叶子 */
			let _fd = 0;
			if( DP.children && DP.children.length>0 ){
				let _list = [];
				_fd = this.doFilter(
					DP.children, keyword, _list );
				if( _fd > 0 ){
					nDP.children = _list;
					retList.push( nDP );
					find ++;
				}
			}
			if( _fd==0 ){   /* b.处理叶子节点了。 */
				let hasKey = DP.deptName.includes(keyword);
				if( hasKey ){
					retList.push( nDP );
					find ++;
				}
			}
		}
		return find;
	},

	/* 【M6】显示添加对话框。【TODO】*/

	/* 【M7】添加部门。【TODO】*/

	/* 【M8】更新部门。【TODO】*/

	/* 【M9】保存部门。【TODO】*/

	/* 【M10】删除部门。【TODO】*/
	
	confirmDelete( id, deptName ){
		deleteDept( id )
		.then(
			(resp)=>{
				this.$message("删除【"+ deptName +"】部门成功。");
				this.opMode = "";
				this.clearSearch();
				this.reloadList();
			}
		);	
	},	

	//9.批量删除部门
	doBatchDelete(){
		
	},
	
	copyDP( DP ){
		return {
			id: DP.id, 
			deptName: DP.deptName,
			parentId: DP.parentId,
			pIds: DP.pIds,
			deptDesc: DP.deptDesc,
			isLeap: DP.isLeap, 
			level: DP.level
		};
	},
	
	clearSearch(){
		this.searchForm = {
			id: "",
			deptName: ""
		};
	},

  },    /* [METHOD-END] */
  
  /*{3}生命周期方法*/
  created(){
	this.reloadList();
  }    /*【CREATED】【END】*/

}
</script>

<style>
span.el-tree-node__label{
	font-size:15px;
}
.el-button--text span{
	font-size:15px;
}
</style>
