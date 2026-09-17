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
					<el-steps :space="200" :active="2" finish-status="success">
					  <el-step title="基本信息"></el-step>
					  <el-step title="规格参数"></el-step>
					  <el-step title="销售属性"></el-step>
					  <el-step title="SKU设置"></el-step>
					  <el-step title="保存完成"></el-step>
					</el-steps>
					<!-- 【2.1】步骤条【END】 -->
				</el-col>
			</el-row>

			<!-- 2.销售属性表单. -->
			<el-row :gutter="20" style="margin-top:20px;">
				<el-col :span="20">
					<el-form :model="saleAttrForm" ref="saleAttrFormRef"
					    label-width="120px">

						<!-- 动态生成销售属性表单 -->
						<el-form-item v-for="(item, index) in saleAttrForm.list"
							style="width:650px;"
							:key="index" :label="item.attrName">
							<el-select
								v-if="item.valueType==1"
								v-model="item.attrValue"
								key="single"
								filterable
								allow-create
								style="width:550px"
								default-first-option>
								<el-option v-for="opt in item.options" :key="opt" :label="opt" :value="opt"></el-option>
							</el-select>
							<el-select
								v-else
								v-model="item.valueArr"
								key="multi"
								multiple
								filterable
								clearable
								allow-create
								size="medium"
								style="width:550px"
								default-first-option>
								<el-option v-for="opt in item.options" :key="opt" :label="opt" :value="opt"></el-option>
							</el-select>
						</el-form-item>

						<el-form-item>
							<el-button type="primary" @click="saveSaleAttrData">下一步</el-button>
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
import { listByCategory } from '@/api/pms_goodsAttr.js'
import { saveSaleAttrValues } from '@/api/pms_publish.js'

export default {
  name: 'SetSaleAttr',
  data(){
     return {
		/* 1.类别ID. */
		categoryId: '',
		/* 2.属性类型.[2=销售属性] */
		attrType: 2,
		/* 3.销售属性表单. */
		saleAttrForm:{
			list: []
		},
     }
  },

  /* 【方法区】【START】 */
  methods:{
	/* 【M2】保存此页销售属性表单值 */
	saveSaleAttrData(){
		const list = this.saleAttrForm.list.map(
			item => {
				let obj = {
					attrId: item.id,
					valueType: item.valueType
				};
				obj.attrValue = (item.valueType==1)
					? item.attrValue
					: item.valueArr.join(";");
				return obj;
			}
		);
		let _pubKey = window.localStorage.getItem("pubKey");
		let data = {
			pubKey: _pubKey,
			attrList: list
		};
		saveSaleAttrValues( data )
		.then(
			resp=>{
				console.log( resp );
				/* [工单03] 跳转到 SKU 设置页 */
				this.$router.push({
					name: 'setSku',
					params:{ pubKey: window.localStorage.getItem("pubKey") }
				});
			}
		);
	},

  },  /*【METHODS】【END】 */

  /*【1】【生命周期】【mounted】 */
  mounted(){
	this.categoryId = this.$route.params.categoryId;
	console.log("【系统】销售属性-类别ID:"+ this.categoryId);
	listByCategory( this.categoryId, this.attrType )
	.then(
		resp=>{
			let list = resp.data;
			list.forEach(
				o=>{
					/* 可选项列表：从 attrValue 模板解析 */
					o.options = o.attrValue ? o.attrValue.split(";") : [];
					if( o.valueType==2 ){
						o.valueArr = [];  /* 多选初始为空，用户手动选择 */
					} else {
						o.attrValue = '';  /* 单值初始为空 */
					}
				}
			);
			this.saleAttrForm.list = [...list];
		}
	);
  },
}
</script>

<style scoped>
.el-button span { color:white; }
.el-card .el-dialog__body{ padding-top:0px; }
</style>