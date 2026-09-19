<template>
  <div>
	<el-card class="box-card">
	  <div slot="header" class="clearfix">
		<el-breadcrumb separator="/">
		  <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
		  <el-breadcrumb-item>商品维护</el-breadcrumb-item>
		  <el-breadcrumb-item>商品管理</el-breadcrumb-item>
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
		<el-form-item label="状态">
			<el-select v-model="searchForm.publishStatus" placeholder="全部" clearable style="width:100px;">
				<el-option label="上架" :value="1"></el-option>
				<el-option label="下架" :value="0"></el-option>
			</el-select>
		</el-form-item>
		<el-form-item label="推荐">
			<el-select v-model="searchForm.isRecommend" placeholder="全部" clearable style="width:100px;">
				<el-option label="已推荐" :value="1"></el-option>
				<el-option label="未推荐" :value="0"></el-option>
			</el-select>
		</el-form-item>
		<el-form-item label="关键词">
			<el-input v-model="searchForm.keyword" placeholder="商品名称/编号" style="width:160px;"
				@keyup.enter.native="doSearch"></el-input>
		</el-form-item>
		<el-form-item label="商品编号">
			<el-input v-model="searchForm.goodsSn" placeholder="请输入编号" style="width:130px;" clearable></el-input>
		</el-form-item>
		<el-form-item label="价格区间">
			<el-input-number v-model="searchForm.minPrice" :min="0" :controls="false" placeholder="最低价" style="width:100px;"></el-input-number>
			<span style="margin:0 5px;">~</span>
			<el-input-number v-model="searchForm.maxPrice" :min="0" :controls="false" placeholder="最高价" style="width:100px;"></el-input-number>
		</el-form-item>
		<el-form-item>
			<el-button type="primary" @click="doSearch">搜索</el-button>
			<el-button @click="doReset">重置</el-button>
		</el-form-item>
	</el-form>

	<!-- Tab 分类 -->
	<el-tabs v-model="activeTab" @tab-click="handleTabClick" style="margin-bottom:10px;">
		<el-tab-pane label="全部" name="all"></el-tab-pane>
		<el-tab-pane label="销售中" name="onSale"></el-tab-pane>
		<el-tab-pane label="已下架" name="offSale"></el-tab-pane>
		<el-tab-pane label="库存预警" name="lowStock"></el-tab-pane>
	</el-tabs>

	  <!-- 批量操作栏 -->
	  <div style="margin-bottom:10px;">
		<el-button type="success" size="small" :disabled="multipleSelection.length==0" @click="handleBatchStatus(1)">批量上架</el-button>
		<el-button type="warning" size="small" :disabled="multipleSelection.length==0" @click="handleBatchStatus(0)">批量下架</el-button>
		<el-button type="primary" size="small" :disabled="multipleSelection.length==0" @click="handleBatchRecommend(1)">批量推荐</el-button>
		<el-button type="info" size="small" :disabled="multipleSelection.length==0" @click="handleBatchRecommend(0)">取消推荐</el-button>
		<el-button type="danger" size="small" :disabled="multipleSelection.length==0" @click="handleBatchDelete">批量删除</el-button>
		<span style="color:#999; margin-left:10px;">已选 {{ multipleSelection.length }} 项</span>
	  </div>

	  <!-- 商品表格 -->
	  <el-table :data="goodsList" style="width: 100%" border @selection-change="handleSelectionChange">
		<el-table-column type="selection" width="45"></el-table-column>
		<el-table-column label="封面图" width="80" align="center">
			<template slot-scope="scope">
				<div class="goods-thumb">
					<img v-if="scope.row.mainImage" :src="coverUrl(scope.row.mainImage)" />
					<span v-else>{{ scope.row.goodsName ? scope.row.goodsName.charAt(0) : '图' }}</span>
				</div>
			</template>
		</el-table-column>
		<el-table-column prop="goodsSn" label="商品编号" width="100"></el-table-column>
		<el-table-column prop="goodsName" label="商品名称" width="160" :show-overflow-tooltip="true"></el-table-column>
		<el-table-column label="价格" width="90">
			<template slot-scope="scope">
				<span style="color:#f56c6c; font-weight:bold;">￥{{ scope.row.price }}</span>
			</template>
		</el-table-column>
		<el-table-column label="库存" width="80" align="center">
			<template slot-scope="scope">
				<span :style="{color: scope.row.stock < 20 ? '#f56c6c' : ''}">{{ scope.row.stock }}</span>
			</template>
		</el-table-column>
		<el-table-column prop="saleCount" label="销量" width="70" align="center"></el-table-column>
		<el-table-column label="上架状态" width="80">
			<template slot-scope="scope">
				<el-tag :type="scope.row.publishStatus==1?'success':'info'" size="mini">
					{{ scope.row.publishStatus==1?'上架':'下架' }}
				</el-tag>
			</template>
		</el-table-column>
		<el-table-column label="推荐" width="70" align="center">
			<template slot-scope="scope">
				<el-switch
					v-model="scope.row.isRecommend"
					:active-value="1"
					:inactive-value="0"
					active-color="#67c23a"
					inactive-color="#dcdfe6"
					@change="handleToggleRecommend(scope.row)">
				</el-switch>
			</template>
		</el-table-column>
		<el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
		<el-table-column label="操作" width="160" fixed="right">
			<template slot-scope="scope">
				<el-button v-if="scope.row.publishStatus==1" size="mini" type="warning"
					@click="handleUpdateStatus(scope.row, 0)">下架</el-button>
				<el-button v-else size="mini" type="success"
					@click="handleUpdateStatus(scope.row, 1)">上架</el-button>
				<el-button size="mini"
					:disabled="scope.row.publishStatus==1"
					@click="handleSku(scope.row)">规格</el-button>
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
import {
	getGoodsList,
	updateGoodsStatus,
	batchUpdateStatus,
	batchDelete,
	toggleRecommend,
	batchRecommend
} from '@/api/pms_goods.js'
import { getCategoryList } from '@/api/pms_category.js'
import { list as getBrandList } from '@/api/pms_brand.js'
import { goodsCoverUrl } from '@/utils/imageUrl'

export default {
  name: 'GoodsManage',

  data () {
    return {
		searchForm: {
			categoryId: null,
			brandId: null,
			publishStatus: null,
			isRecommend: null,
			keyword: "",
			goodsSn: "",
			minPrice: undefined,
			maxPrice: undefined,
			lowStock: null
		},
		activeTab: 'all',
		categoryOptions: [],
		brandOptions: [],
		goodsList: [],
		curPage: 1,
		pageSize: 10,
		totalCount: 0,
		multipleSelection: [],
	}
  },

  created () {
	this.getCategoryOptions();
	this.getBrandOptions();
	this.getGoodsData();
  },

  methods:{
    coverUrl( fileName ){
      return goodsCoverUrl( fileName );
    },
	getGoodsData(){
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
		if( this.searchForm.publishStatus != null && this.searchForm.publishStatus !== '' ){
			params.publishStatus = this.searchForm.publishStatus;
		}
		if( this.searchForm.isRecommend != null && this.searchForm.isRecommend !== '' ){
			params.isRecommend = this.searchForm.isRecommend;
		}
		if( this.searchForm.keyword && this.searchForm.keyword.trim() ){
			params.keyword = this.searchForm.keyword.trim();
		}
		if( this.searchForm.goodsSn && this.searchForm.goodsSn.trim() ){
			params.goodsSn = this.searchForm.goodsSn.trim();
		}
		if( this.searchForm.minPrice != null ){
			params.minPrice = this.searchForm.minPrice;
		}
		if( this.searchForm.maxPrice != null ){
			params.maxPrice = this.searchForm.maxPrice;
		}
		if( this.searchForm.lowStock != null ){
			params.lowStock = this.searchForm.lowStock;
		}
		getGoodsList( params )
		.then(resp=>{
			var data = resp.data || []
			data.forEach(function(item) {
				item.isRecommend = Number(item.isRecommend)
				item.publishStatus = Number(item.publishStatus)
			})
			this.goodsList = data;
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
		this.getGoodsData();
	},

	handleTabClick(tab){
		this.searchForm.publishStatus = null;
		this.searchForm.lowStock = null;
		if( tab.name === 'onSale' ){
			this.searchForm.publishStatus = 1;
		} else if( tab.name === 'offSale' ){
			this.searchForm.publishStatus = 0;
		} else if( tab.name === 'lowStock' ){
			this.searchForm.lowStock = 1;
		}
		this.curPage = 1;
		this.getGoodsData();
	},

	doReset(){
		this.searchForm = {
			categoryId: null,
			brandId: null,
			publishStatus: null,
			isRecommend: null,
			keyword: "",
			goodsSn: "",
			minPrice: undefined,
			maxPrice: undefined,
			lowStock: null
		};
		this.activeTab = 'all';
		this.curPage = 1;
		this.getGoodsData();
	},

	reloadPage(page){
		this.curPage = page;
		this.getGoodsData();
	},

	handleSizeChange(size){
		this.pageSize = size;
		this.curPage = 1;
		this.getGoodsData();
	},

	/* 单个上下架 */
	handleUpdateStatus(row, publishStatus){
		updateGoodsStatus( row.id, publishStatus )
		.then(resp=>{
			this.$message.success( publishStatus==1 ? '上架成功' : '下架成功' );
			this.getGoodsData();
		});
	},

	/* 批量上下架 */
	handleBatchStatus(publishStatus){
		var ids = this.multipleSelection.map(function(item){ return item.id; });
		var tip = publishStatus==1 ? '上架' : '下架';
		this.$confirm('确认批量' + tip + '选中的 ' + ids.length + ' 件商品？', '提示', {
			type: 'warning'
		}).then(()=>{
			batchUpdateStatus(ids, publishStatus).then(()=>{
				this.$message.success('批量' + tip + '成功');
				this.getGoodsData();
			});
		}).catch(()=>{});
	},

	/* 批量删除 */
	handleBatchDelete(){
		var ids = this.multipleSelection.map(function(item){ return item.id; });
		this.$confirm('确认删除选中的 ' + ids.length + ' 件商品？删除后不可恢复。', '警告', {
			type: 'error'
		}).then(()=>{
			batchDelete(ids).then(()=>{
				this.$message.success('批量删除成功');
				this.getGoodsData();
			});
		}).catch(()=>{});
	},

	/* 单个推荐切换 */
	handleToggleRecommend(row){
		toggleRecommend(row.id, row.isRecommend).then(()=>{
			this.$message.success(row.isRecommend==1 ? '已加入推荐' : '已取消推荐');
		});
	},

	/* 批量推荐 */
	handleBatchRecommend(isRecommend){
		var ids = this.multipleSelection.map(function(item){ return item.id; });
		var tip = isRecommend==1 ? '加入推荐' : '取消推荐';
		batchRecommend(ids, isRecommend).then(()=>{
			this.$message.success('批量' + tip + '成功');
			this.getGoodsData();
		});
	},

	/* 规格编辑(上架锁定校验) */
	handleSku(row){
		if(row.publishStatus == 1){
			this.$message.warning('上架商品不可编辑，请先下架');
			return;
		}
		this.$message.info('规格编辑功能开发中');
	},

	handleSelectionChange(val){
		this.multipleSelection = val;
	}
  }
}
</script>

<style scoped>
.goods-thumb {
	width: 50px;
	height: 50px;
	background: #f0f2f5;
	border-radius: 4px;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #c0c4cc;
	font-size: 16px;
	margin: 0 auto;
	overflow: hidden;
}
.goods-thumb img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}
</style>
