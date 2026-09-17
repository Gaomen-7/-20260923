<template>
    <div>
		<!-- 1. 这是面包屑导航条.. -->
		<el-breadcrumb separator-class="el-icon-arrow-right" 
			style="height:30px;margin-top:15px;padding-left:15px;">
		  <el-breadcrumb-item :to="{path:'/'}">首页</el-breadcrumb-item>
		  <el-breadcrumb-item>商品管理</el-breadcrumb-item>
		  <el-breadcrumb-item>品牌列表</el-breadcrumb-item>
		</el-breadcrumb>

        <!-- 2.设置一个卡片组件 -->
        <el-card class="box-card">		
			<!-- 2.1~[搜索栏]【START】 -->
			<el-row :gutter="20">
				<el-col :span="16">
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
							@click="showAddBox();">添加品牌</el-button>
					</el-form-item>
				</el-form>
				</el-col>
			</el-row>
			<!-- 2.1~[搜索栏]【END】 -->
			
			<el-row :gutter="20">  
				<el-col :span="20">
				<!-- 2.2~品牌表格【START】 -->
				<el-table
					:data="brandList"
					height="410"
					style="width: 100%">
					
					<el-table-column
						prop="id" label="编号" width="80">
					</el-table-column>					
					<el-table-column
						prop="brandName" label="品牌名称" width="120">
					</el-table-column>	
					
					<el-table-column	
						prop="imgUrl" label="logo" width="125">
						<!-- {T1}模板~~图片 -->
						<template slot-scope="scope">
							<el-popover placement="top-start" title="" trigger="hover">
							  <img :src="scope.row.imgUrl" alt="" height="120" />
							  <img slot="reference" :src="scope.row.imgUrl" 
								style="width:100px;height:45px;border:1px solid #CCC;">
							</el-popover>
						</template>
					</el-table-column>
					
					<el-table-column
						prop="showStatus" label="显示状态"
						width="80">					  
						<!-- {T2}模板~~开关 -->
						<template slot-scope="scope">
							<el-tooltip :content="'状态值: '+ scope.row.showStatus"
									placement="top">
							  <el-switch
								 v-model="scope.row.showStatus"
								 active-color="#13ce66"
								 inactive-color="#ff4949"
								 @change="statusChange(scope.row)"
								 :active-value="1"
								 :inactive-value="0">
							  </el-switch>
							</el-tooltip>
						</template>					  
					</el-table-column>

					<el-table-column	
					  prop="createDate"
					  label="创建日期"
					  width="180">
					</el-table-column>
					
					<el-table-column label="操作">		
						<!-- {T3}模板~~BUTTON -->
						<template slot-scope="scope">
							<el-button
							  size="mini"
							  @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
							<el-button
							  size="mini"
							  type="danger"
							  @click="handleDelete(scope.$index, scope.row)">删除</el-button>
							<el-button
							  size="mini"
							  @click="handleAssociate(scope.$index, scope.row)">关联</el-button>	  
						</template>
					</el-table-column>
				</el-table>		<!-- 品牌表格【END】-->	
				
				<!-- 2.3~这是分页条【START】 -->
				<el-pagination
				  background
				  layout="prev, pager, next"
				  :total="totalCount"
				  :current-page="curPage"
				  @current-change="reloadPage"
				  style="margin-top:10px;">
				</el-pagination>
				</el-col>
			</el-row>

		   <!-- T5-品牌对话框【START】-->
           <!-- 粘贴代码时, 请覆盖掉我 -->

		   <!-- T5-品牌对话框【END】-->
           <el-dialog :title="dialogTitle" :visible.sync="showFormBox" width="60%" 
				:before-close="handleClose" style="padding: 0px 0px;"
				@close="onClose" >
				<el-form :model="brandForm" ref="brandFormRef" 
					:rules="brandFormRules">
					<el-form-item label="品牌名称" prop="brandName" 
						style="margin-top:0px;">
						<el-input v-model="brandForm.brandName"></el-input> 
					</el-form-item>
					
					<el-form-item label="品牌logo"
						style="margin-top:5px;"
						v-if="opMode=='add'">
						<!-- 3. 这里要放一个文件上传组件 -->
						<el-upload
							class="upload-demo" ref="upload"
							action=""
							:on-preview="handlePreview"
							:on-remove="handleRemove"
							:on-success="handleSuccess"
							:before-upload="beforeUpload"
							:auto-upload="false"
							:http-request="httpRequest"
							list-type="picture"
							:file-list="fileList">
								<el-button slot="trigger" size="small" type="primary">
								选取文件
								</el-button>
								<el-button style="margin-left: 10px;" size="small"
									type="success" @click="submitUpload">
									上传到服务器
								</el-button>
							<div slot="tip" class="el-upload_tip">
							只能上传 jpg/png 文件,
							且不超过 500kb
							</div>
							<el-tag>{{tips}}</el-tag>
						</el-upload>
					</el-form-item>
					<el-form-item label="显示状态" prop="showStatus" 
						style="margin-top:5px;">
						<!-- 3. 这里要放一个 el-switch 组件 -->
						<el-switch
						  v-model="brandForm.showStatus"
						  active-color="#13ce66"
						  inactive-color="#ff4949">
						</el-switch>
					</el-form-item>
					<el-form-item label="品牌介绍" prop="brandDesc" 
						style="margin-top:5px;">
						<el-input
							type="textarea"
							:rows="3"
							placeholder="请输入品牌介绍"
							v-model="brandForm.brandDesc">
						</el-input>						
					</el-form-item> 			  
					<!-- [添加其它同类项] -->
				</el-form>	
				<span slot="footer" class="dialog-footer">
					<el-button @click="showFormBox=false">取消</el-button>
					<el-button type="primary" :disabled="disableSave"
						@click="saveBrandData">确定</el-button>
				</span>
			</el-dialog>
		    <!-- 3~品牌对话框【END】   -->

        </el-card>
    </div>
</template>

<script>
//1.导入相关 api
/* --这里需要导入 API */
import { 
	list,
	addBrand,
	updateBrand,
	uploadFile,
	deleteBrand
} from '@/api/pms_brand.js'
import { pickForm } from '@/utils/common.js'

export default {
  name: 'brandlist',
  data(){
     return {
		/* 1.搜索框 */
		searchForm:{
			id:'',
			brandName:'',
		},
		
		/*2.对话框 */
		showFormBox:false,
		opMode:'add',
		dialogTitle:'添加品牌',
		brandFormRules:{
			brandName:[
				{required:true, message:'请输入品牌名称',trigger:'blur'},
				{min:2, max:15, message:'品牌名称长度:(2-15)',trigger:'blur'},
			],
			brandDesc:[
				{required:true, message:'请输入品牌描述',trigger:'blur'},
			],		
		},		

		/*2.2.品牌的表单数据 */
		brandForm:{
			brandName:'',
			logoName:'',     /*  */
			status:false,    /*1.给 switch 使用的*/
			showStatus:'',   /*2.实体类使用 */
			info:'',
			brandDesc:''			
		},
		tips:'请上传图片(否则无法提交)',
		disableSave: true,		
		
		/*3.品牌列表*/
		brandList:[],
		curPage:1,
		pageSize:10,
		totalCount:0,
		
		/* 4.文件上传 */
		fileList:[],
		imgUrl:'',   /* 保存返回的上传图片的地址 */
     }
  },
  
  /* 【方法区】【START】 */
  methods:{
  
	/* 【M1】获取品牌列表入口【TODO】*/

	
	/* 【M2】搜索方法【TODO】*/

	
	/* 【M3】分页按钮触发方法【TODO】*/

	
	/* 【M4】对话框相关方法 */
	handleClose(){
	   /* --请填入代码(3)--  */
	},

	/* 【M5】关闭对话框相关方法 */
	onClose(){
	    /* --请填入代码(4)--  */
	},
	
	/* 【M6】显示添加品牌【TODO】*/


	/* 【M7】保存品牌数据(新增、修改)【TODO】*/

	
	/* 【M8】新增品牌【TODO】*/


	/* 【M9】点击编辑按钮【TODO】*/

	
	/* 【M10】更新品牌【TODO】*/


	/* 【M11】删除事件处理【TODO】*/


	/* 【M12】确认删除处理【TODO】*/

	
	/* 【M13】文件上传相关方法【--】*/
	handlePreview(){ },	
	handleRemove(row){},
	handleSuccess(){
	    /* --请填入代码(9)--  */
	},	
	handleAssociate(){	
	
	},	

	/* 【M14】在上传文件之前做数据校验【TODO】*/

	
	/* 【M15】封装参数执行上传【TODO】*/


	/* 【M16】提交上传【TODO】*/


  },  /*【METHODS】【END】 */

  /*【生命周期】【mounted】 */
  mounted(){
	 this.doSearch();
  },
  
  /*【生命周期】【created】 */
  created(){
	 /* --请填入代码(14)--  */
  }
}
</script>

<style scoped>
.el-button span { color:white; }
.el-card .el-dialog__body{ padding-top:0px; }
</style>