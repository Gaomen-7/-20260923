<template>
  <div>
	<el-card class="box-card">
	  <div slot="header" class="clearfix">
		<span>品牌管理</span>
	  </div>

	  <!-- 搜索区 -->
	  <el-form :inline="true" :model="searchForm">
		<el-form-item label="品牌ID">
			<el-input v-model="searchForm.id" placeholder="请输入品牌ID" style="width:150px;"></el-input>
		</el-form-item>
		<el-form-item label="品牌名称">
			<el-input v-model="searchForm.brandName" placeholder="请输入品牌名称" style="width:180px;"></el-input>
		</el-form-item>
		<el-form-item>
			<el-button type="primary" @click="doSearch">查询</el-button>
			<el-button type="primary" @click="showAddBox">添加品牌</el-button>
		</el-form-item>
	  </el-form>

	  <!-- 品牌表格 -->
	  <el-table :data="brandList" style="width: 100%">
		<el-table-column prop="id" label="编号" width="80"></el-table-column>
		<el-table-column prop="brandName" label="品牌名称" width="150"></el-table-column>
		<el-table-column label="品牌LOGO" width="120">
			<template slot-scope="scope">
				<el-image v-if="scope.row.imgUrl" :src="scope.row.imgUrl" style="width:80px; height:40px;" fit="contain"></el-image>
				<span v-else style="color:#c0c4cc; font-size:12px;">无图</span>
			</template>
		</el-table-column>
		<el-table-column prop="showStatus" label="显示状态" width="100">
			<template slot-scope="scope">
				<el-tag :type="scope.row.showStatus==1?'success':'info'">
					{{ scope.row.showStatus==1?'显示':'不显示' }}
				</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="createDate" label="创建日期" width="160"></el-table-column>
		<el-table-column label="操作">
			<template slot-scope="scope">
				<el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
				<el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
			</template>
		</el-table-column>
	  </el-table>

	  <!-- 分页 -->
	  <el-pagination
		background
		layout="total, sizes, prev, pager, next, jumper"
		:total="totalCount"
		:current-page="curPage"
		@current-change="reloadPage"
		style="margin-top:10px;">
	  </el-pagination>
	</el-card>

	<!-- 添加/编辑品牌对话框 -->
	<el-dialog :title="dialogTitle" :visible.sync="showFormBox" width="50%"
			:before-close="handleClose" :close-on-click-modal="false">
		<el-form :model="brandForm" label-width="100px">
			<el-form-item label="品牌名称">
				<el-input v-model="brandForm.brandName" placeholder="请输入品牌名称"></el-input>
			</el-form-item>
			<el-form-item label="显示状态">
				<el-switch v-model="brandForm.showStatus"
					active-text="显示" inactive-text="不显示"
					:active-value="true" :inactive-value="false"></el-switch>
			</el-form-item>
			<el-form-item label="品牌LOGO">
				<el-upload
					ref="upload"
					action=""
					:show-file-list="false"
					:auto-upload="false"
					:http-request="httpRequest"
					:before-upload="beforeUpload">
					<el-button size="small" type="primary" @click="submitUpload">点击上传</el-button>
					<div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
				</el-upload>
				<el-image v-if="brandForm.logoName" :src="brandForm.imgUrl"
					style="width:100px; height:60px; margin-top:10px;" fit="contain"></el-image>
				<div v-if="tips" style="color:#67c23a; margin-top:5px;">{{ tips }}</div>
			</el-form-item>
		</el-form>
		<span slot="footer" class="dialog-footer">
			<el-button @click="showFormBox=false">取消</el-button>
			<el-button type="primary" :disabled="disableSave" @click="saveBrandData">确定</el-button>
		</span>
	</el-dialog>
  </div>
</template>

<script>
import { pickForm } from '@/utils/common.js'
import { brandLogoUrl } from '@/utils/imageUrl'
import {
	list,
	addBrand,
	updateBrand,
	deleteBrand,
	uploadFile
} from '@/api/pms_brand.js'

export default {
  name: 'Brand',

  /* {1} 数据区 */
  data () {
    return {
		/* 搜索表单 */
		searchForm: {
			id: "",
			brandName: ""
		},
		/* 品牌列表 */
		brandList: [],
		curPage: 1,
		pageSize: 10,
		totalCount: 0,
		/* 品牌表单 */
		brandForm: {},
		dialogTitle: "",
		showFormBox: false,
		opMode: "",
		disableSave: true,    /* 保存按钮是否禁用(必须上传图片后才可提交) */
		tips: ""               /* 上传提示信息 */
	}
  },

  /* {2}方法区 */
  methods:{
	/* 【M1】获取品牌列表入口【TODO】 */
	getBrandList(searchForm, page){
		/* 1.赋值当前页. */
		/* 3.调用 API 方法请求数据. */
		list( this.curPage, this.pageSize, searchForm )
		.then(resp=>{
			/* 3.1.接收后端列表数据. [成功之后]*/
			this.brandList = resp.data;
			/* 3.2.设置图片的显示地址。 */
			this.brandList.forEach(
				br=>{
					/* 后端列表接口返回字段为 logoUrl（兼容旧字段 logoName） */
					br.logoName = br.logoName || br.logoUrl;
					br.imgUrl = brandLogoUrl( br.logoName || br.logoUrl );
				}
			);
			this.totalCount = resp.total;
		});
	},

	/* 【M2】搜索方法【TODO】 */
	doSearch(){
		let id = this.searchForm.id;
		if( id!="" && !/^\d+$/.test(id) ){
			this.$message("你输入的 ID 只允许是数字。");
			return;
		}
		/* 2.抓取表单非空数据项。 */
		let param = pickForm(this.searchForm);
		/* 3.重新加载品牌列表。 */
		this.getBrandList(param, 1);
	},

	/* 【M3】分页按钮触发方法【TODO】 */
	reloadPage( newPage ){
		/* 点击第2页, 第3页, 跳转到某一页, 执行以下逻辑。 */
		/* 1.抓取表单非空数据项。 */
		let param = pickForm(this.searchForm);
		/* 2.重新加载品牌列表。 */
		this.getBrandList(param, newPage);
	},

	/* 【M6】显示添加品牌【TODO】 */
	showAddBox(){
		/* {ps}点击【添加品牌】按钮 ==>【执行以下流程】*/
		this.opMode = "add";   /* 设置为添加模式。 */
		this.dialogTitle = "添加新品牌";
		this.showFormBox = true;   /* 打开开关 */
		this.brandForm = {};
		this.tips = "";
		/* 必须上传图片之后方可提交。 */
		this.disableSave = true;   /* 把提交开关关闭。 */
	},

	/* 【M7】保存品牌数据(新增、修改)【TODO】 */
	saveBrandData(){
		/* {ps}点击【保存】按钮 ==>【执行以下流程】*/
		if( this.opMode=="add" ){
			this.doAddBrand();
		}else{
			this.doUpdateBrand();
		}
	},

	/* 【M8】新增品牌(写入后台)【TODO】 */
	doAddBrand(){
		let bdName = this.brandForm.brandName;
		/* 页面的值: true/false, 后台: 1/0 */
		/* 1.这里要做数据的转换. */
		let status = this.brandForm.showStatus;
		status = (status==true) ? 1 : 0;
		this.brandForm.showStatus = status;
		/* 2.调用 API 方法请求后台. */
		addBrand( this.brandForm )
		.then(resp=>{
			this.onSaveSuccess("添加", bdName);
		});
	},

	/* 【M9】点击品牌列表中【编辑】按钮【TODO】 */
	handleEdit(index, row){
		let brandName = row.brandName;
		this.opMode = "edit";
		this.dialogTitle = `修改【${brandName}】品牌`;
		this.showFormBox = true;
		this.brandForm = {...row};   /* 对象拷贝 */
		this.disableSave = false;     /* 允许提交. */
		this.tips = "";

		let status = this.brandForm.showStatus;
		status = (status==1)? true : false;
		this.brandForm.showStatus = status;
	},

	/* 【M10】更新品牌【TODO】 */
	doUpdateBrand(){
		let bdName = this.brandForm.brandName;
		let status = this.brandForm.showStatus;
		status = (status==true)? 1 : 0;
		this.brandForm.showStatus = status;
		updateBrand( this.brandForm )
		.then(
			resp=>{
				this.onSaveSuccess("更新", bdName);
			}
		);
	},

	/* 【M11】删除事件处理【TODO】
	点击【删除】品牌, 给一个提示框【确认】。
	*/
	handleDelete(index, row){
		let id = row.id;
		let brandName = row.brandName;
		this.$confirm(`确认要删除【${brandName}】品牌吗?`,
			"安全警告",{
			type:"warning"
		}).then(()=>{
			/* 点击【确认】==>执行【删除】. */
			this.confirmDelete(id, brandName);
		}).catch(()=>{});
	},

	/* 【M12】确认删除处理【TODO】 */
	confirmDelete(id, brandName){
		deleteBrand( id )
		.then(
			resp=>{
				this.$message(`删除【${brandName}】品牌成功。`);
				/* 重新加载列表. */
				let param = pickForm(this.searchForm);
				this.getBrandList(param, 1);
			}
		);
	},

	/* 【M8.2】保存成功之后执行的逻辑处理【TODO】 */
	onSaveSuccess( opType, bdName ){
		/* opType: [添加, 更新] */
		this.$message(`${opType}【${bdName}】品牌成功!`);
		/* 3.隐藏对话框(复位). */
		this.showFormBox = false;
		/* 4.复位搜索表单. */
		this.searchForm.id = "";
		this.searchForm.brandName = "";
		/* 5.重新加载列表. */
		let param = pickForm(this.searchForm);
		this.getBrandList(param, 1);
	},

	/* 【M14】在上传文件之前做数据校验【TODO】 */
	beforeUpload( file ){
		/* 1.计算文件的大小。 */
		let size = file.size / 1024 / 1024;
		/* 2.校验文件大小。 */
		if( size>2 ){
			this.$message("上传的文件不能 2 MB.");
			return false;
		}
		return true;
	},

	/* 【M15】封装上传单参数【执行上传】【TODO】
	对应 SpringBoot 后台, 方法返回的数据如下:
	R.ok()
	  .put("logoUri", logoUri)
	  .put("fileName", newName);
	*/
	httpRequest( param ){
		/* 1.创建表单对象. */
		let FD = new FormData();
		FD.append("file", param.file);
		FD.append("userId", "100");  /* 假的用户ID */
		/* 2.调用 api 方法执行上传. */
		uploadFile( FD )
		.then(
			resp=>{
				let fileName = resp.fileName;
				let logoUri = resp.logoUri;
				this.tips = "图片已经上传(现在可以提交)";
				this.$message("图片上传成功。");
				/* 1.把提交锁定开关关闭【false不锁定】.*/
				this.disableSave = false;
				/* 2.把文件名保存到 brand 表单。 */
				this.brandForm.logoName = fileName;
				this.brandForm.imgUrl = brandLogoUrl( fileName );
			}
		);
	},

	/* 【M16】提交上传【TODO】 */
	submitUpload(){
		/* 1.触发文件上传控件提交动作。 */
		this.$refs.upload.submit();
	},

	/* 关闭对话框 */
	handleClose(){
		this.showFormBox = false;
	}
  },

  /* {3}生命周期方法 */
  created(){
	 let param = pickForm(this.searchForm);
	 this.getBrandList(param, 1);
  }

}
</script>

<style>
</style>
