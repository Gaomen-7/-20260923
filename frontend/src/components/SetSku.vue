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
					<el-steps :space="200" :active="3" finish-status="success">
					  <el-step title="基本信息"></el-step>
					  <el-step title="规格参数"></el-step>
					  <el-step title="销售属性"></el-step>
					  <el-step title="SKU设置"></el-step>
					  <el-step title="保存完成"></el-step>
					</el-steps>
					<!-- 【2.1】步骤条【END】 -->
				</el-col>
			</el-row>

			<!-- SKU 表格 -->
			<el-table :data="skuRows" border style="margin-top:20px" ref="skuTable">
				<!-- 展开行：图片选择 + 折扣 + 满减 -->
				<el-table-column type="expand">
					<template slot-scope="scope">
						<el-row :gutter="20" style="padding:20px;">
							<el-col :span="12">
								<div>
									<h4>SKU图片（从图集选择）</h4>
									<div v-for="img in albumList" :key="img"
										style="display:inline-block;margin:5px">
										<el-image :src="imgUrl(img)"
											fit="cover"
											style="width:80px;height:80px;">
											<div slot="error" style="display:flex;align-items:center;justify-content:center;height:100%;background:#f5f7fa;">
												<i class="el-icon-picture-outline"></i>
											</div>
										</el-image>
										<div style="margin-top:5px;">
											<el-checkbox v-model="scope.row.albumImages" :label="img">选</el-checkbox>
											<el-radio v-model="scope.row.defaultImage" :label="img">默认</el-radio>
										</div>
									</div>
									<div style="margin-top:10px;color:#909399;font-size:12px;">
										已选{{ scope.row.albumImages ? scope.row.albumImages.length : 0 }}张
									</div>
								</div>
							</el-col>
							<el-col :span="12">
								<div>
									<h4>折扣设置</h4>
									<div v-for="(d, i) in scope.row.discounts" :key="'d'+i"
										style="margin-bottom:5px;">
										<span>满</span>
										<el-input-number v-model="d.minQuantity" :min="1" size="small"
											style="width:80px;margin:0 5px;"></el-input-number>
										<span>件打</span>
										<el-input-number v-model="d.discountRate" :min="0" :max="10" :precision="1" size="small"
											style="width:80px;margin:0 5px;"></el-input-number>
										<span>折</span>
										<el-button type="text" @click="scope.row.discounts.splice(i,1)">删除</el-button>
									</div>
									<el-button type="text" @click="addDiscount(scope.row)">+ 添加折扣</el-button>
								</div>
								<div style="margin-top:15px;">
									<h4>满减设置</h4>
									<div v-for="(r, i) in scope.row.reductions" :key="'r'+i"
										style="margin-bottom:5px;">
										<span>满</span>
										<el-input-number v-model="r.fullPrice" :min="0" :precision="2" size="small"
											style="width:80px;margin:0 5px;"></el-input-number>
										<span>元减</span>
										<el-input-number v-model="r.reducePrice" :min="0" :precision="2" size="small"
											style="width:80px;margin:0 5px;"></el-input-number>
										<span>元</span>
										<el-button type="text" @click="scope.row.reductions.splice(i,1)">删除</el-button>
									</div>
									<el-button type="text" @click="addReduction(scope.row)">+ 添加满减</el-button>
								</div>
							</el-col>
						</el-row>
					</template>
				</el-table-column>

				<!-- 启用复选框 -->
				<el-table-column label="启用" width="60" align="center">
					<template slot-scope="scope">
						<el-checkbox v-model="scope.row.enabled"></el-checkbox>
					</template>
				</el-table-column>

				<!-- 动态属性列 -->
				<el-table-column v-for="attrName in attrNameList" :key="attrName" :label="attrName" width="120">
					<template slot-scope="scope">
						<span>{{ scope.row.attrValues[attrName] }}</span>
					</template>
				</el-table-column>

				<!-- SKU 名称 -->
				<el-table-column label="SKU名称" width="180">
					<template slot-scope="scope">
						<el-input v-model="scope.row.skuName" size="small" placeholder="自动生成"></el-input>
					</template>
				</el-table-column>

				<!-- 标题 -->
				<el-table-column label="标题" width="150">
					<template slot-scope="scope">
						<el-input v-model="scope.row.skuTitle" size="small" placeholder="可选"></el-input>
					</template>
				</el-table-column>

				<!-- 副标题 -->
				<el-table-column label="副标题" width="150">
					<template slot-scope="scope">
						<el-input v-model="scope.row.skuSubtitle" size="small" placeholder="可选"></el-input>
					</template>
				</el-table-column>

				<!-- 价格 -->
				<el-table-column label="价格" width="120">
					<template slot-scope="scope">
						<el-input-number v-model="scope.row.price" :min="0" :precision="2" size="small"
							style="width:100px;"></el-input-number>
					</template>
				</el-table-column>

				<!-- 操作 -->
				<el-table-column label="操作" width="80" align="center">
					<template slot-scope="scope">
						<el-button type="text" @click="toggleExpand(scope.$index)">展开/收起</el-button>
					</template>
				</el-table-column>
			</el-table>

			<!-- 按钮组 -->
			<el-row style="margin-top:20px;">
				<el-col :span="20">
					<el-button @click="goBack">上一步</el-button>
					<el-button type="primary" @click="saveSkuData">保存并下一步</el-button>
				</el-col>
			</el-row>

		</el-card>
		<!-- 【2】卡片方块【END】 -->
    </div>
</template>

<script>
/* 1.导入相关 api. */
import { getSaleAttr, getAlbumList, generateSku, saveSkuCache } from '@/api/pms_publish.js'
import { listByCategory } from '@/api/pms_goodsAttr.js'

export default {
  name: 'SetSku',
  data(){
     return {
		pubKey: '',
		categoryId: '',
		attrNameList: [],     /* 动态列名，如 ['机身颜色','存储容量','版本'] */
		skuRows: [],          /* SKU表格数据 */
		albumList: [],        /* 图集文件名列表 */
     }
  },

  methods: {
	/* 图片URL拼接 */
	imgUrl( fileName ){
		return 'http://localhost:8090/mall-sys/PublishGoods/showImg/album/' + fileName;
	},

	/* 展开/收起行 */
	toggleExpand( index ){
		this.$refs.skuTable.toggleRowExpansion( this.skuRows[index] );
	},

	/* 添加折扣 */
	addDiscount( row ){
		row.discounts.push({ minQuantity: 1, discountRate: 10 });
	},

	/* 添加满减 */
	addReduction( row ){
		row.reductions.push({ fullPrice: 0, reducePrice: 0 });
	},

	/* 上一步 */
	goBack(){
		this.$router.back();
	},

	/* 保存并下一步 */
	saveSkuData(){
		let data = {
			pubKey: this.pubKey,
			skuData: this.skuRows
		};
		saveSkuCache( data )
		.then(
			resp=>{
				this.$router.push({name:'publishComplete', params:{pubKey: this.pubKey}});
			}
		)
		.catch(
			err=>{
				this.$message.error('保存失败：' + err.message);
			}
		);
	},

	/* 加载数据（核心） */
	loadData(){
		this.pubKey = this.$route.params.pubKey;
		if( !this.pubKey ){
			this.$message.error('缺少 pubKey 参数');
			return;
		}
		/* 并行加载：销售属性选中值 + 图集（含categoryId） */
		Promise.all([ getSaleAttr(this.pubKey), getAlbumList(this.pubKey) ])
		.then(
			([ saleResp, albumResp ])=>{
				/* 暂存saleResp供后续then使用 */
				this.saleResp = saleResp;
				/* 从albumResp获取categoryId */
				this.categoryId = albumResp.categoryId || (albumResp.data && albumResp.data.categoryId);
				this.albumList = albumResp.albumList || (albumResp.data && albumResp.data.albumList) || [];
				if( !this.categoryId ){
					this.$message.error('无法获取 categoryId，请确保已保存基本信息');
					return Promise.reject('categoryId缺失');
				}
				/* 获取属性模板（含attrName） */
				return listByCategory( this.categoryId, 2 );
			}
		)
		.then(
			attrResp=>{
				/* listByCategory 返回 R 对象，真正的数组在 .data 里 */
				let attrList = attrResp.data || [];
				let saleAttrList = (this.saleResp.attrList || (this.saleResp.data && this.saleResp.data.attrList) || []);
				/* 构建attrItems：匹配选中值和属性模板的attrName */
				let attrItems = [];
				attrList.forEach(
					attr => {
						let selected = saleAttrList.find(
							sel => sel.attrId === attr.id
						);
						if( selected && selected.attrValue ){
							attrItems.push({
								attrId: attr.id,
								attrName: attr.attrName,
								attrValue: selected.attrValue
							});
						}
					}
				);
				if( attrItems.length===0 ){
					this.$message.warning('未选择任何销售属性');
					return;
				}
				/* 调用generateSku */
				let generateData = { attrItems: attrItems };
				return generateSku( generateData );
			}
		)
		.then(
			result=>{
				/* generateSku返回 R.ok().put("skuRows", ...) */
				let rows = result.skuRows || (result.data && result.data.skuRows) || [];
				this.skuRows = rows;
				if( rows.length>0 ){
					/* 从第一行attrValues提取attrNameList */
					this.attrNameList = Object.keys( rows[0].attrValues || {} );
				}
				console.log('【系统】SKU行数:', this.skuRows.length);
			}
		)
		.catch(
			err=>{
				console.error('【系统】加载数据失败:', err);
			}
		);
	},
  },

  mounted(){
	this.loadData();
  },
}
</script>

<style scoped>
.el-button span { color:white; }
.el-card .el-dialog__body{ padding-top:0px; }
</style>