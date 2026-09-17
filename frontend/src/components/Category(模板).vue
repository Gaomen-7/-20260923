<template>
  <div>
	<!-- 【D01:START】这是页签栏 -->
	<el-tabs type="border-card">
	  <el-tab-pane>
		<span slot="label"><i class="el-icon-date"></i>分类列表</span>		
	  </el-tab-pane>
	  <el-tab-pane label="添加分类">添加分类</el-tab-pane>
	</el-tabs>
    <!-- 【D01:END】 -->
  
	<el-row :gutter="24">	  
	  <el-col :span="6">	  
		<!-- 【D02:START】左边的卡片 -->
		<el-card class="box-card">
		
		  <div slot="header" class="clearfix">
			  <span>请选择分类</span>
			  <el-button style="float: right; padding: 3px 0" type="text">
			  添加一级类别
			  </el-button>			
		  </div>

		  <!-- 树形菜单区域 [START]-->
			<el-tree
				:data="categoryList"
				show-checkbox
				node-key="id"
				default-expand-all
				@node-click="handleNodeClick"
				:expand-on-click-node="false">
				<span class="custom-tree-node" slot-scope="{ node, data }">
				
					<span>{{ data.categoryName }}</span>
					<span>
					  <el-button
						type="text"
						size="small"
						v-if="data.level<3"
						@click.stop
						@click="()=>append(data)">
						添加
					  </el-button>
					  <!--
						<el-button
							type="text"
							size="small"
							@click="()=>edit( data )">
							编辑
						  </el-button>
					  -->
					  <el-button
						type="text"
						size="small"
						v-if="data.level==3"
						@click.stop
						@click="()=>associate(node, data)">
						设置关联
					  </el-button>			  
					  <el-button
						type="text"
						size="small"
						v-if="data.isLeap==true"
						@click.stop
						@click="()=>remove(node, data)">
						删除
					  </el-button>
					</span>
				</span>
			</el-tree>

		</el-card>
		<!-- 【D02:END】 -->
	  </el-col>	 
	  
	  <el-col :span="18">
	  
		<el-card class="box-card" v-show="opType=='edit' || opType=='add'">		
			<!-- 【D03:START】【el-form】 -->
			<el-form
				:model="categoryForm" :rules="categoryRules" 
				ref="cateFormRef">
				  <div>{{formTitle}}</div>	  
				  <el-form-item label="类别名称" prop="categoryName" 
					style="margin-top:5px;">
					<el-input v-model="categoryForm.categoryName"></el-input> 
				  </el-form-item>
				  <el-form-item label="父节点ID" prop="parentId" 
					style="margin-top:5px;">
					<el-input v-model="categoryForm.parentId" readonly></el-input>
					<input type="hidden" :value="categoryForm.pIds" />
				  </el-form-item>
				  <el-form-item label="显示状态" prop="showStatus" 
					style="margin-top:5px;">
						<el-switch
							v-model="categoryForm.showStatus"
							active-color="green"
							inactive-color="#CCC"
							active-text="显示"
							inactive-text="隐藏" />
						</el-switch>
				  </el-form-item>
				  <el-form-item label="排序号" prop="sort" 
					style="margin-top:5px;">
					<el-input v-model="categoryForm.sort"></el-input> 
				  </el-form-item>
				  <!-- [添加其它同类项] -->
				  <span>
					 <el-button id="confirm" type="primary" 					
						@click="saveCategory">{{btnText}}</el-button>
				  </span>
			</el-form>
			<!-- 【D03:END】 -->
		</el-card>
		
		<el-card class="box-card" v-show="opType=='brandList'">
			<!-- 【D04:START】【品牌搜索】 -->
			<div style="line-height:22px;font-size:18px;">
				<label>当前分类:</label>
				<label id="topic">
					{{curSelect.categoryName}}
					( {{curSelect.categoryId}} )
				</label>
			</div>
			<div style="margin-top:10px">			
				<el-form :inline="true" 
					:model="searchForm" class="demo-form-inline">
					<el-form-item label="品牌ID:">
						<el-input placeholder="请输入品牌ID" 
						   v-model="searchForm.id" 
						   clearableclear="doSearch"></el-input>
					</el-form-item>					   
					<el-form-item label="品牌名称:">				
						<el-input placeholder="请输入品牌名称" 
						   v-model="searchForm.brandName" 
						   clearableclear="doSearch">
						   <el-button slot="append"
							 @click="doSearch">点击搜索</el-button>
						</el-input>
					</el-form-item>
					<el-form-item>
						<el-button type="primary" 
							@click="submitSelection">
							提交关联</el-button>
					</el-form-item>
				</el-form>
				
			</div>
			<!-- 【D04:END】【品牌搜索】 -->
			
			<!-- 【D05:START】【el-table】 -->
				<el-table
					ref="brandTable"
					:data="brandList" height="410"
					:show-checkbox="true" style="width: 100%"
					:row-key="row=>row.id"
					@selection-change="handleSelectChange">
					<el-table-column type="selection"
					  prop="isAssociate"
					  :reserve-selection="true"
					  width="55">
					</el-table-column>	
					<el-table-column
					  prop="id"
					  label="编号"
					  width="80">
					</el-table-column>
					<el-table-column
					  prop="brandName"
					  label="品牌名称"
					  width="120">
					</el-table-column>
					<el-table-column	
						prop="logoUrl" label="logo" width="125">
						<template slot-scope="scope">
							<el-popover placement="top-start" title="" trigger="hover">
							  <img :src="scope.row.logoUrl" alt="" height="120" />
							  <img slot="reference" :src="scope.row.logoUrl" 
								style="width:100px;height:45px;border:1px solid #CCC;">
							</el-popover>
						</template>
					</el-table-column>
					<el-table-column
					  prop="createDate"
					  label="创建日期"
					  width="180">
					</el-table-column>
				</el-table>
			<!-- 【D04:END】 -->
			
			<!-- {3}这是分页条 -->
			<!-- 【D05:START】 -->
			<el-pagination
				  background
				  layout="prev, pager, next"
				  :total="totalCount"
				  :current-page="curPage"
				  @current-change="reloadPage"
				  style="margin-top:10px;" >
			</el-pagination>
			<!-- 【D05:END】 -->
		</el-card>

	  </el-col>
	</el-row>

  </div>
</template>

<script>
import { 
	getCategoryList, associateBrand,
	addCategory, updateCategory,
	deleteCategory
} from '@/api/pms_category.js'
import { pickForm, formatDate } from '@/utils/common.js'
import { listByCategory } from '@/api/pms_brand.js'

export default {
  name: 'Category',
  data () {
    return {
		/* 1.Brand 品牌列表的分页参数 */
		curPage: 1,
		pageSize: 10,
		totalCount: 0,
		brandList: [],
		searchForm:{
			id:'',
			categoryId:'',
			brandName:''
		},
		
		/* 2.Category 类别树形相关参数 */
		categoryList:[],
		defaultProps:{
			children: 'children',
			label: 'categoryName'
		},
		curSelect:{},   /* 当前选择 */
		
		/* 3.类别表单 */	
		categoryForm:{
			id:'',
			categoryName:'',
			parentId:'',
			pIds:'',
			showStatus:'',
			sort:'',
			icons:'icons'
		},
		showForm:false,
		showBrand:false,
		categoryRules:{},
		opType:'',
		formTitle:'',
		btnText:'',
		
		/* 4.其它数据 */
		selectItems:[],  /*记住这里有 s */		
	}
  },    /* DATA-END */
  
  methods:{
	/* 【M0】setLeap 【---】*/
	setLeap(childList, level){
		childList.forEach(
			(D)=>{
				D.level = level;
				console.log("[FOREACH]【%s】【%s】", 
					D.categoryName, D.level );
				let children = D.children;
				if( !children || children.length==0 ){
					D.isLeap = true;
				}else{
					D.isLeap = false;
					this.setLeap( children, level+1 );
				}
			}
		);
	},
  
	/* 【M1】类别节点单击[触发方法] 【TODO】*/
	/* --请填入代码 -- */

	/*
		【M2】追加子节点 【TODO】
		【1级, 2级节点允许添加】
	*/
	/* --请填入代码 -- */

	/*【M3】编辑类别节点【----】 */
	edit( data ){
		console.log( data );
	},
	
	/* 【M4】移除类别节点【TODO】*/
	/* --请填入代码 -- */
	
	/* 【M5】重加载页【TODO】*/
	/* --请填入代码 -- */

	/*
	   【M6】复选框的变化处理代码 【TODO】
		items 就是我们选中的内容。
	*/
	/* --请填入代码 -- */

	/* 【M7】关联品牌【TODO】*/
	/* --请填入代码 -- */
	
	/*【M8】搜索品牌列表(带关联标记的)【TODO】*/
	/* --请填入代码 -- */

	/*【M9】查询列表。【TODO】*/
	/* --请填入代码 -- */
	
	/*【M10】processList()【TODO】*/
	/* --请填入代码 -- */
	
	/*【M11】提交选择。【TODO】*/
	/* --请填入代码 -- */

	/*【M12】提交选择。【TODO】*/
	/* --请填入代码 -- */
	
	/*【M13】保存类别。【TODO】*/
	/* --请填入代码 -- */
	
	/*【M14】添加类别的操作方法。【TODO】*/
	/* --请填入代码 -- */
	
	/*【M15】doUpdateCategory。【TODO】*/
	/* --请填入代码 -- */

	/*【M16】设置表格行选中方法。【TODO】*/
	/* --请填入代码 -- */
	
	/*【M17】加载类别的树形列表。【TODO】*/
	/* --请填入代码 -- */
	
	/*【M18】设置父类别的名称。【TODO】*/
	/* --请填入代码 -- */
	
  },  /* 方法区 [结束] */
  
  //{ps}当 Vue 实例创建完成执行以下内容
  //    [生命周期-勾子函数]
  created(){
	 //1.加载类别的树形列表。
	 this.loadCategoryTree();
  }
}  /* VUE 声明块 [结束]  */
</script>

<style scoped>
span.el-tree-node__label{ font-size:17px; }
button span{
	font-size:16px;
	color:orange;
}
#confirm span{ color:white; }
#topic{
	margin-right:25px;
	font-weight:bold
}
</style>
