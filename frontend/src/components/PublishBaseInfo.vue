<template>
    <div>
		<!-- 【1】面包屑导航条【START】 -->
		<el-breadcrumb separator-class="el-icon-arrow-right"
			style="height:30px;margin-top:15px;padding-left:15px;">
		  <el-breadcrumb-item :to="{path:'/'}">首页</el-breadcrumb-item>
		  <el-breadcrumb-item>商品维护</el-breadcrumb-item>
		  <el-breadcrumb-item>发布商品</el-breadcrumb-item>
		</el-breadcrumb>
		<!-- 【1】面包屑导航条【END】 -->

		<!-- 【2】卡片方块【START】 -->
		<el-card class="box-card">
			<el-row :gutter="20">
				<el-col :span="20">
					<!-- 【2.1】步骤条【START】 -->
					<el-steps :space="200" :active="0" finish-status="success">
					  <el-step title="基本信息"></el-step>
					  <el-step title="规格参数"></el-step>
					  <el-step title="销售属性"></el-step>
					  <el-step title="SKU设置"></el-step>
					  <el-step title="保存完成"></el-step>
					</el-steps>
					<!-- 【2.1】步骤条【END】 -->
				</el-col>
			</el-row>

			<!-- 2.商品详情表单. -->
			<el-row :gutter="20" style="margin-top:20px;">
				<el-col :span="20">
					<el-form :model="spuForm" ref="brandFormRef"
					    label-width="120px" :rules="spuFormRules">

						<!-- 【1】商品名称 -->
						<el-form-item label="商品名称:" required>
							<el-input v-model="spuForm.goodsDetail.goodsName"
							    style="width:650px;"
								placeholder="请输入商品名字"></el-input>
						</el-form-item>

						<!-- 【2】商品描述 -->
						<el-form-item label="商品描述:" required>
							<el-input v-model="spuForm.goodsDetail.goodsDetails"
							    type="textarea" :rows="3"
							    style="width:650px;"
								placeholder="请编写一个简单描述"></el-input>
						</el-form-item>

						<!-- 【3】选择分类【START】 -->
						<el-form-item label="选择分类:" required>
							<el-cascader
								v-model="spuForm.pIdArr"
								:options="categoryList"
								:props="{children:'children', label:'categoryName', value:'id'}"
								placeholder="请选择类别"
								clearable
								@change="cascaderChange"
								style="width:650px;">
							</el-cascader>
						</el-form-item>
						<!-- 【3】选择分类【END】 -->

						<!-- 【4】选择品牌【START】 -->
						<el-form-item label="选择品牌:" required>
							<el-select v-model="spuForm.goodsDetail.brandId"
								placeholder="请选择品牌"
								style="width:650px;">
								<el-option
									v-for="item in brandOptions"
									:key="item.value"
									:label="item.label"
									:value="item.value">
								</el-option>
							</el-select>
						</el-form-item>
						<!-- 【4】选择品牌【END】 -->

						<!-- 【5】商品重量 -->
						<el-form-item label="商品重量(Kg):">
							<el-input-number v-model="spuForm.goodsDetail.weight"
								:precision="3" :step="0.1" :min="0"
								style="width:200px;"></el-input-number>
						</el-form-item>

						<!-- 【6】设置积分 -->
						<el-form-item label="设置积分:">
							<span style="margin-right:10px;">积分抵消</span>
							<el-input-number v-model="spuForm.pointRule.allowDeduct"
								:min="0" size="small" style="width:120px;"></el-input-number>
							<span style="margin:0 10px 0 20px;">返回积分</span>
							<el-input-number v-model="spuForm.pointRule.rewardPoint"
								:min="0" size="small" style="width:120px;"></el-input-number>
						</el-form-item>

						<!-- 【7】商品介绍（主图）【START】 -->
						<el-form-item label="商品介绍:">
							<el-upload
								ref="mainUpload"
								action=""
								list-type="picture-card"
								:show-file-list="false"
								:auto-upload="false"
								:on-change="uploadChange"
								:http-request="httpRequest"
								:before-upload="beforeUpload">
								<img v-if="imageUrl" :src="imageUrl"
									class="upload-img" />
								<i v-else class="el-icon-plus"></i>
							</el-upload>
						</el-form-item>
						<!-- 【7】商品介绍（主图）【END】 -->

						<!-- 【8】商品图集【START】 -->
						<el-form-item label="商品图集:">
							<el-upload
								action=""
								list-type="picture-card"
								:show-file-list="false"
								:auto-upload="false"
								multiple
								:on-change="albumChange"
								:before-upload="beforeUpload">
								<i class="el-icon-plus"></i>
							</el-upload>
							<!-- 已上传的图集预览 -->
							<div v-for="(img, index) in albumImages"
								:key="index"
								class="album-preview">
								<el-image :src="img.url"
									fit="cover"
									style="width:100px;height:100px;"></el-image>
							</div>
						</el-form-item>
						<!-- 【8】商品图集【END】 -->

						<el-form-item style="margin-top:30px;">
							<el-button type="success" @click="savePublishData"
								style="width:300px;">下一步：设置基本参数</el-button>
						</el-form-item>

					</el-form>
				</el-col>
			</el-row>

		</el-card>
		<!-- 【2】卡片方块【END】 -->
    </div>
</template>

<script>
/* 1.导入相关 api. */
import { getCategoryList } from '@/api/pms_category.js'
import { getBrandOptions } from '@/api/pms_brand.js'
import { pickForm } from '@/utils/common.js'
import { uploadImage, savePublishBase } from '@/api/pms_publish.js'

export default {
  name: 'PublishBaseInfo',
  data(){
     return {
		/* 1.三级列表的绑定项. */
		categoryList: [],
		/* 2.品牌选项数据. */
		brandOptions: [],
		/* 3.主图地址. */
		imageUrl: '',
		/* 4.图集预览列表. */
		albumImages: [],
		/* 5.禁止保存.[true:不能保存] */
		disableSave: true,
		/* 6.商品基本信息表单. */
		spuForm:{
			/* 1.商品基本信息. */
			goodsDetail:{
				goodsName:"",       /* SPU的名称. */
				goodsDetails:"",    /* SPU的描述. */
				weight:0,           /* 商品重量. */
				mainImage:"",       /* 商品主图. */
				categoryId:"",      /* 类别ID. */
				brandId:"",         /* 品牌ID. */
			},
			/* 2.商品图集. */
			spuAlbum:{
				images:[], /* t01.png, t02.png.. */
			},
			/* 3.积分规则设置. */
			pointRule:{
				rewardPoint:0,     /* 返回积分(返还点数). */
				allowDeduct:0,     /* 积分抵消(允许扣除). */
				work:''
			},
			pIdArr:[]
		},
		/* 7.表单的验证. */
		spuFormRules:{

		},
     }
  },

  /* 【方法区】【START】 */
  methods:{
	/* 【M1】多级菜单改变 */
	cascaderChange( arr ){
		if( arr.length==3 ){
			let categoryId = arr[2];
			this.spuForm.goodsDetail.categoryId = categoryId;
			getBrandOptions( categoryId )
			.then(
				resp=>{
					this.brandOptions = resp.data;
				}
			);
		}else{
			this.spuForm.goodsDetail.categoryId = "";
		}
	},

	/* 【M2】文件上传相关 */
	handleSuccess(res, file) { },

	/* --- 主图上传 --- */
	uploadChange( file ){
		this.$refs.mainUpload.submit();
	},

	beforeUpload( file ){
		/* 1.计算文件的大小。 */
		let size = file.size / 1024 / 1024;
		/* 2.校验文件大小。 */
		if( size>2 ){
			this.$message("【提示】上传的文件不能 2 MB.");
			return false;
		}
		return true;
	},

	httpRequest( param ){
		/* 1.创建表单对象. */
		let FD = new FormData();
		FD.append("file", param.file);
		/* 2.这里指定我上传的是主图 type=1 */
		FD.append("type", 1);
		let BASE = "http://localhost:8090/mall-sys";
		/* 3.调用 api 方法执行上传. */
		uploadImage( FD )
		.then(
			resp=>{
				this.$message("图片上传成功。");
				this.disableSave = false;
				this.imageUrl = BASE + resp.logoUri;
				this.spuForm.goodsDetail.mainImage = resp.fileName;
			}
		);
	},

	/* --- 图集上传 --- */
	albumChange( file ){
		if( file.status === 'ready' ){
			let FD = new FormData();
			FD.append("file", file.raw);
			/* type=2 表示图集 */
			FD.append("type", 2);
			let BASE = "http://localhost:8090/mall-sys";
			uploadImage( FD )
			.then(
				resp=>{
					this.$message("图集图片上传成功。");
					this.albumImages.push({
						url: BASE + resp.logoUri
					});
					this.spuForm.spuAlbum.images.push(resp.fileName);
				}
			);
		}
	},

	/* 保存发布数据 - 基本信息 */
	savePublishData() {
		savePublishBase( this.spuForm )
		.then(
			resp=>{
				let pubKey = resp.pubKey;
				/* 1.把 pubKey 保存到浏览器本地。 */
				window.localStorage.setItem("pubKey", pubKey);
				console.log("【系统】pubKey:"+ pubKey);
				let _categoryId = this.spuForm.goodsDetail.categoryId;
				console.log("【系统】categoryId:"+ _categoryId);
				/* 2.实现实现跳转到第二页，并传参数。 */
				this.$router.push({
					name: 'setGoodsAttr',
					params:{ categoryId: _categoryId }
				});
			}
		);
	},

  },  /*【METHODS】【END】 */

  /*【1】【生命周期】【mounted】 */
  mounted(){
	 getCategoryList()
	 .then(
		resp=>{
			this.categoryList = resp.data;
		}
	 );
  },
  /*【2】【生命周期】【created】 */
  created(){
  }
}
</script>

<style scoped>
.el-button span { color:white; }
.el-card .el-dialog__body{ padding-top:0px; }
.upload-img { width:100%; height:100%; object-fit:cover; border-radius:4px; }
.album-preview { display:inline-block; margin-right:10px; margin-top:10px; }
</style>
