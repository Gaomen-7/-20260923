<template>
  <div>
	<el-card class="box-card">
	  <div slot="header" class="clearfix">
		<el-breadcrumb separator="/">
		  <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
		  <el-breadcrumb-item>商品维护</el-breadcrumb-item>
		  <el-breadcrumb-item>SKU 管理</el-breadcrumb-item>
		</el-breadcrumb>
	  </div>

	  <!-- 筛选区 -->
	  <el-form :inline="true" :model="searchForm">
		<el-form-item label="分类">
			<el-select v-model="searchForm.categoryId" placeholder="全部分类" clearable style="width:140px;">
				<el-option v-for="cat in categoryOptions" :key="cat.id"
					:label="cat.categoryName" :value="cat.id"></el-option>
			</el-select>
		</el-form-item>
		<el-form-item label="品牌">
			<el-select v-model="searchForm.brandId" placeholder="全部品牌" clearable style="width:140px;">
				<el-option v-for="br in brandOptions" :key="br.id"
					:label="br.brandName" :value="br.id"></el-option>
			</el-select>
		</el-form-item>
		<el-form-item label="价格区间">
			<el-input-number v-model="searchForm.minPrice" :min="0" :controls="false"
				placeholder="最小价" style="width:100px;"></el-input-number>
			<span style="margin:0 5px;">~</span>
			<el-input-number v-model="searchForm.maxPrice" :min="0" :controls="false"
				placeholder="最大价" style="width:100px;"></el-input-number>
		</el-form-item>
		<el-form-item label="关键词">
			<el-input v-model="searchForm.keyword" placeholder="请输入SKU名称" style="width:160px;"
				@keyup.enter.native="doSearch"></el-input>
		</el-form-item>
		<el-form-item>
			<el-button type="primary" @click="doSearch">搜索</el-button>
			<el-button @click="doReset">重置</el-button>
		</el-form-item>
	  </el-form>

	  <!-- SKU表格 -->
	  <el-table :data="skuList" style="width: 100%" border
		@selection-change="handleSelectionChange">
		<el-table-column type="expand" width="50">
			<template slot-scope="scope">
				<el-form label-width="100px" class="sku-detail">
					<el-form-item label="SKU标题"><span>{{ scope.row.skuTitle }}</span></el-form-item>
					<el-form-item label="副标题"><span>{{ scope.row.skuSubtitle }}</span></el-form-item>
					<el-form-item label="SKU描述"><span>{{ scope.row.skuDesc }}</span></el-form-item>
					<el-form-item label="分类ID"><span>{{ scope.row.categoryId }}</span></el-form-item>
					<el-form-item label="品牌ID"><span>{{ scope.row.brandId }}</span></el-form-item>
					<el-form-item label="默认图"><span>{{ scope.row.defaultImage || '无' }}</span></el-form-item>
				</el-form>
			</template>
		</el-table-column>
		<el-table-column type="selection" width="45"></el-table-column>
		<el-table-column prop="skuId" label="skuId" width="70"></el-table-column>
		<el-table-column label="默认图片" width="90" align="center">
			<template slot-scope="scope">
				<div class="sku-thumb">
					<span>{{ scope.row.defaultImage ? scope.row.defaultImage.charAt(0) : '无' }}</span>
				</div>
			</template>
		</el-table-column>
		<el-table-column prop="skuName" label="SKU名称" width="180" :show-overflow-tooltip="true"></el-table-column>
		<el-table-column label="价格" width="90">
			<template slot-scope="scope">
				<span style="color:#f56c6c;">￥{{ scope.row.price }}</span>
			</template>
		</el-table-column>
		<el-table-column label="库存" width="70" align="center">
			<template slot-scope="scope">
				<span :style="{color: scope.row.stock < 20 ? '#f56c6c' : ''}">{{ scope.row.stock }}</span>
			</template>
		</el-table-column>
		<el-table-column prop="saleCount" label="销量" width="70" align="center"></el-table-column>
		<el-table-column label="操作" width="200">
			<template slot-scope="scope">
				<el-button size="mini" type="primary"
					@click="handlePreview(scope.row)">预览</el-button>
				<el-button size="mini" type="success"
					@click="handleComment(scope.row)">评论</el-button>
				<el-dropdown trigger="click" @command="cmd=>handleMore(cmd, scope.row)"
					style="margin-left:5px;">
					<el-button size="mini">更多<i class="el-icon-arrow-down el-icon--right"></i></el-button>
					<el-dropdown-menu slot="dropdown">
						<el-dropdown-item command="edit">编辑</el-dropdown-item>
						<el-dropdown-item command="album">图片集</el-dropdown-item>
						<el-dropdown-item command="delete" divided>删除</el-dropdown-item>
					</el-dropdown-menu>
				</el-dropdown>
			</template>
		</el-table-column>
	  </el-table>

	  <!-- 分页 -->
	  <el-pagination
		background
		layout="total, sizes, prev, pager, next, jumper"
		:total="totalCount"
		:current-page="curPage"
		:page-size="pageSize"
		:page-sizes="[10, 20, 50]"
		@current-change="reloadPage"
		@size-change="handleSizeChange"
		style="margin-top:10px;">
	  </el-pagination>
	</el-card>
  </div>
</template>

<script>
import { getSkuList } from '@/api/pms_goods.js'
import { getCategoryList } from '@/api/pms_category.js'
import { list as getBrandList } from '@/api/pms_brand.js'

export default {
  name: 'SkuManage',

  data () {
    return {
		searchForm: {
			categoryId: null,
			brandId: null,
			minPrice: undefined,
			maxPrice: undefined,
			keyword: ""
		},
		categoryOptions: [],
		brandOptions: [],
		skuList: [],
		curPage: 1,
		pageSize: 10,
		totalCount: 0,
		multipleSelection: []
	}
  },

  created () {
	this.getCategoryOptions();
	this.getBrandOptions();
	this.getSkuData();
  },

  methods:{
	getSkuData(){
		let params = {
			page: this.curPage,
			limit: this.pageSize
		};
		if( this.searchForm.categoryId != null && this.searchForm.categoryId !== '' ){
			params.categoryId = this.searchForm.categoryId;
		}
		if( this.searchForm.brandId != null && this.searchForm.brandId !== '' ){
			params.brandId = this.searchForm.brandId;
		}
		if( this.searchForm.minPrice != null ){
			params.minPrice = this.searchForm.minPrice;
		}
		if( this.searchForm.maxPrice != null ){
			params.maxPrice = this.searchForm.maxPrice;
		}
		if( this.searchForm.keyword && this.searchForm.keyword.trim() ){
			params.keyword = this.searchForm.keyword.trim();
		}
		getSkuList( params )
		.then(resp=>{
			this.skuList = resp.data;
			this.totalCount = resp.total;
		});
	},

	getCategoryOptions(){
		getCategoryList()
		.then(resp=>{
			let options = [];
			this.flattenCategory( resp.data, 0, options );
			this.categoryOptions = options;
		});
	},
	flattenCategory(nodes, level, out){
		if( !nodes ) return;
		nodes.forEach( node=>{
			out.push({
				id: node.id,
				categoryName: '　'.repeat(level) + node.categoryName
			});
			this.flattenCategory( node.children, level+1, out );
		});
	},

	getBrandOptions(){
		getBrandList( 1, 100, {} )
		.then(resp=>{
			this.brandOptions = resp.data;
		});
	},

	doSearch(){
		this.curPage = 1;
		this.getSkuData();
	},

	doReset(){
		this.searchForm = {
			categoryId: null,
			brandId: null,
			minPrice: undefined,
			maxPrice: undefined,
			keyword: ""
		};
		this.curPage = 1;
		this.getSkuData();
	},

	reloadPage(page){
		this.curPage = page;
		this.getSkuData();
	},

	handleSizeChange(size){
		this.pageSize = size;
		this.curPage = 1;
		this.getSkuData();
	},

	handlePreview(row){
		this.$message.info('功能开发中');
	},

	handleComment(row){
		this.$message.info('功能开发中');
	},

	handleMore(cmd, row){
		this.$message.info('功能开发中');
	},

	handleSelectionChange(val){
		this.multipleSelection = val;
	}
  }
}
</script>

<style scoped>
.sku-detail {
	padding-left: 30px;
}
.sku-thumb {
	width: 44px;
	height: 44px;
	background: #f0f2f5;
	border-radius: 4px;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #c0c4cc;
	font-size: 14px;
	margin: 0 auto;
}
</style>
