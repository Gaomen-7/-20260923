<template>
  <div id="app">
	<div v-if="isLoggedIn">
		<table id="NAV">
			<tr>
				<td @click="select(1)">
					<a href="#/dashBoard" :style="{color:(P==1?ON:OFF)}">数据全景</a>
				</td>
				<td @click="select(2)">
					<a href="#/user" :style="{color:(P==2?ON:OFF)}">用户管理</a>
				</td>
				<td @click="select(3)">
					<a href="#/dept" :style="{color:(P==3?ON:OFF)}">部门管理</a>
				</td>
				<td @click="select(4)">
					<a href="#/category" :style="{color:(P==4?ON:OFF)}">商品类别</a>
				</td>
				<td @click="select(5)">
					<a href="#/brand" :style="{color:(P==5?ON:OFF)}">品牌管理</a>
				</td>
				<td @click="select(6)">
					<a href="#/attrGroup" :style="{color:(P==6?ON:OFF)}">属性分组</a>
				</td>
				<td @click="select(8)">
					<a href="#/goodsAttr" :style="{color:(P==8?ON:OFF)}">规格参数</a>
				</td>
				<td @click="select(9)">
					<a href="#/saleAttr" :style="{color:(P==9?ON:OFF)}">销售属性</a>
				</td>				
				<td @click="select(10)">
					<a href="#/goodsManage" :style="{color:(P==10?ON:OFF)}">商品管理</a>
				</td>
				<td @click="select(11)">
					<a href="#/publishBaseInfo" :style="{color:(P==11?ON:OFF)}">发布商品</a>
				</td>
				<td @click="select(16)">
					<a href="#/skuManage" :style="{color:(P==16?ON:OFF)}">SKU管理</a>
				</td>
				<td @click="select(12)">
					<a href="#/orderManage" :style="{color:(P==12?ON:OFF)}">订单管理</a>
				</td>
				<td @click="select(17)">
					<a href="#/couponManage" :style="{color:(P==17?ON:OFF)}">活动管理</a>
				</td>
				<td @click="select(18)">
					<a href="#/memberManage" :style="{color:(P==18?ON:OFF)}">会员管理</a>
				</td>
				<td @click="select(19)">
					<a href="#/inventoryManage" :style="{color:(P==19?ON:OFF)}">库存管理</a>
				</td>
				<td @click="select(20)">
					<a href="#/advertManage" :style="{color:(P==20?ON:OFF)}">广告设置</a>
				</td>
				<td @click="select(21)">
					<a href="#/analysis/behavior" :style="{color:(P==21?ON:OFF)}">用户行为</a>
				</td>
				<td @click="select(22)">
					<a href="#/analysis/order" :style="{color:(P==22?ON:OFF)}">订单分析</a>
				</td>
				<td @click="select(23)">
					<a href="#/analysis/review" :style="{color:(P==23?ON:OFF)}">评价分析</a>
				</td>
				<td @click="select(24)">
					<a href="#/analysis/chat" :style="{color:(P==24?ON:OFF)}">客服交流</a>
				</td>
				<td @click="handleLogout">
					<a href="javascript:;" :style="{color:OFF}">退出系统</a>
				</td>
			</tr>
		</table>
	</div>
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
  data(){
	return {
		P:1,
		index:1,
		ON:'yellow',
		OFF:'#CCC',
		isLoggedIn: false
	}
  },
  methods:{
	setColor( i ){
		return (i==this.index)?"yellow":"#CCC";
	},
	select(_index){
		this.P = _index
		localStorage.setItem("P",_index);
	},
	handleLogout(){
	  if(confirm('确定要退出登录吗？')){
	    window.sessionStorage.removeItem('token')
	    window.sessionStorage.removeItem('user')
	    this.isLoggedIn = false
	    this.$router.push('/login')
	  }
	}
  },
  created(){
	 let p = localStorage.getItem("P");
	 if( p ){
		this.P = p;
	 }
	 // 初始化登录状态
	 this.isLoggedIn = !!window.sessionStorage.getItem('token')
  },  
  computed:{
	computedColor(){
		var i = 1;
		return (i==this.index)?"yellow":"#CCC";
	}  
  }
}
</script>

<style>
body{ margin:0px; }
#app { color: #2c3e50; }
#NAV {
	border-collapse:collapse;
	border:1px solid #666;
	width:100%;
}
#NAV td{
	border:1px solid #666; height:35px;
	background:#222; width:80px;
	text-align:center;
}
td a:visited{ color:#DDD; }
td a:link{ color:yellow; }
td a:hover{ color:white; }
</style>
