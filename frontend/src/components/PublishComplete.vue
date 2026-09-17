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
					<!-- 【2.1】步骤条【START】（第五步高亮） -->
					<el-steps :space="200" :active="4" finish-status="success">
					  <el-step title="基本信息"></el-step>
					  <el-step title="规格参数"></el-step>
					  <el-step title="销售属性"></el-step>
					  <el-step title="SKU设置"></el-step>
					  <el-step title="保存完成"></el-step>
					</el-steps>
					<!-- 【2.1】步骤条【END】 -->
				</el-col>
			</el-row>

			<!-- 【2.2】成功提示区【START】 -->
			<div style="text-align:center;padding:40px;">
				<i class="el-icon-circle-check"
					style="font-size:64px;color:#67C23A;"></i>
				<h2 style="margin-top:20px;">商品发布成功</h2>
				<p style="margin-top:10px;">商品ID：{{ goodsId }}</p>
				<p>{{ goodsName }}</p>
				<p style="color:#909399;font-size:13px;">商品已默认下架，请到商品管理页确认信息后上架</p>
				<el-row style="margin-top:30px;justify-content:center;">
					<el-button type="primary" @click="goGoodsList">查看商品列表</el-button>
					<el-button @click="goPublish">继续发布商品</el-button>
				</el-row>
			</div>
			<!-- 【2.2】成功提示区【END】 -->
		</el-card>
		<!-- 【2】卡片方块【END】 -->
    </div>
</template>

<script>
/* 1.导入相关 api. */
import { saveComplete } from '@/api/pms_publish.js'

export default {
  name: 'PublishComplete',
  data(){
     return {
		pubKey: '',
		goodsId: '',
		goodsName: '商品发布完成'
     };
  },
  mounted(){
     this.pubKey = this.$route.params.pubKey;
     saveComplete( this.pubKey )
     .then(
        resp=>{
           this.goodsId = resp.goodsId || (resp.data && resp.data.goodsId);
           if( this.goodsId==='' ){
              this.goodsName = '已发布';
           }
        }
     )
     .catch(
        err=>{
           this.$message.error('发布失败：' + err.message);
        }
     );
  },
  methods:{
     /* 查看商品列表（05号工单才实现，先提示） */
     goGoodsList(){
        this.$message.info('商品管理页即将上线');
     },
     /* 继续发布商品 */
     goPublish(){
        this.$router.push('/publishBaseInfo');
     }
  }
}
</script>
