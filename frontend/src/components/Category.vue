<template>
  <div>
	<el-row :gutter="24">
	  <!-- 左侧：类别树形列表 -->
	  <el-col :span="8">
		<el-card class="box-card">
		  <div slot="header" class="clearfix">
			<span>类别管理</span>
			<el-button style="float: right; padding: 3px 0" type="text" @click="loadCategoryTree">刷新</el-button>
		  </div>
		  <el-tree
			:data="categoryList"
			:props="defaultProps"
			accordion
			node-key="id"
			@node-click="handleNodeClick">
			<span class="custom-tree-node" slot-scope="{ node, data }">
				<span>{{ data.categoryName }}</span>
				<span style="float:right; margin-right:5px;">
					<el-button type="text" size="mini" @click.stop="append(data)">添加</el-button>
					<el-button type="text" size="mini" @click.stop="associate(node, data)">关联品牌</el-button>
					<el-button type="text" size="mini" style="color:#f56c6c;" @click.stop="remove(node, data)">删除</el-button>
				</span>
			</span>
		  </el-tree>
		</el-card>
	  </el-col>

	  <!-- 右侧：品牌关联列表 -->
	  <el-col :span="16" v-if="opType=='brandList'">
		<el-card class="box-card">
		  <div slot="header" class="clearfix">
			<span>【{{ curSelect.categoryName }}】关联品牌列表</span>
		  </div>

		  <!-- 搜索区 -->
		  <el-form :inline="true" :model="searchForm">
			<el-form-item label="品牌名称">
				<el-input v-model="searchForm.brandName" placeholder="请输入品牌名称" style="width:180px;"></el-input>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click="doSearch">查询</el-button>
				<el-button type="success" @click="submitSelection">提交关联</el-button>
			</el-form-item>
		  </el-form>

		  <!-- 品牌表格（带复选框） -->
		  <el-table
			ref="brandTable"
			:data="brandList"
			style="width: 100%"
			@selection-change="handleSelectChange">
			<el-table-column type="selection" width="55"></el-table-column>
			<el-table-column prop="id" label="编号" width="80"></el-table-column>
			<el-table-column prop="brandName" label="品牌名称" width="150"></el-table-column>
			<el-table-column label="品牌LOGO" width="120">
				<template slot-scope="scope">
					<el-image :src="scope.row.logoUrl" style="width:80px; height:40px;" fit="contain"></el-image>
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
	  </el-col>
	</el-row>

	<!-- 添加/编辑类别对话框 -->
	<el-dialog :title="formTitle" :visible.sync="showFormBox" width="40%"
			:before-close="handleClose" :close-on-click-modal="false">
		<el-form :model="categoryForm" label-width="100px">
			<el-form-item label="类别名称" prop="categoryName">
				<el-input v-model="categoryForm.categoryName" placeholder="请输入类别名称"></el-input>
			</el-form-item>
			<el-form-item label="显示状态" prop="showStatus"
				style="margin-top:5px;">
				<el-switch
					v-model="categoryForm.showStatus"
					active-color="green"
					inactive-color="#CCC"
					:active-value="1"
					:inactive-value="0"
					active-text="显示"
					inactive-text="隐藏" />
			</el-form-item>
		</el-form>
		<span slot="footer" class="dialog-footer">
			<el-button @click="showFormBox=false">取消</el-button>
			<el-button type="primary" @click="saveCategory">{{ btnText }}</el-button>
		</span>
	</el-dialog>
  </div>
</template>

<script>
import { pickForm, formatDate } from '@/utils/common.js'
import {
	getCategoryList,
	addCategory,
	updateCategory,
	deleteCategory,
	associateBrand
} from '@/api/pms_category.js'
import { listByCategory } from '@/api/pms_brand.js'

export default {
  name: 'Category',

  /* {1} 数据区 */
  data () {
    return {
		/* 类别树形列表 */
		categoryList: [],
		defaultProps: {
			children: "children",
			label: "categoryName"
		},
		/* 类别表单 */
		categoryForm: {},
		opType: "",            /* 操作类型: add / edit / brandList */
		btnText: "",           /* 按钮文字 */
		formTitle: "",         /* 对话框标题 */
		showFormBox: false,    /* 对话框显示开关 */

		/* 品牌列表（关联用） */
		brandList: [],
		curPage: 1,
		pageSize: 10,
		totalCount: 0,
		searchForm: {
			categoryId: "",
			brandName: ""
		},
		selectItems: [],       /* 已选中的品牌 */

		/* 当前选中的类别信息 */
		curSelect: {
			categoryId: "",
			categoryName: "",
			deleteIds: [],
			brands: []
		}
	}
  },

  /* {2}方法区 */
  methods:{
	/* 【M1】类别【节点】点击==>【触发方法】【TODO】 */
	handleNodeClick(row, node, component){
		let pName = row.parentName;
		let cName = row.categoryName;
		this.opType = "edit";      /* 操作类型. */
		this.btnText = "保存修改";
		this.formTitle = `编辑类别【${cName}】【父节点: ${pName}】`;
		this.categoryForm = {...row};
		/* 1.设置类别的显示状态(适配 UI 界面) */
		// 注：el-switch 已使用 active-value="1"/inactive-value="0"，无需手动转换
		this.showFormBox = true;
	},

	/* 【M2】追加子节点【TODO】
	【1级, 2级节点允许添加】
	*/
	append( data ){
		let cName = data.categoryName;
		this.opType = "add";       /* 操作类型:add. */
		this.btnText = "确认添加";
		this.formTitle = `添加子类别【${cName}】`;
		this.categoryForm = {parentId: data.id};
		this.showFormBox = true;
	},

	/* 【M4】移除类别节点【TODO】 */
	remove(node, data){
		let id = data.id;
		let categoryName = data.categoryName;
		this.$confirm(
			`确认要删除【${categoryName}】节点吗?`,
			"安全警告",
			{ type:"warning" }
		).then(()=>{
			deleteCategory(id)
			.then(
				resp=>{
					/* 重新加载树形列表. */
					this.loadCategoryTree();
				}
			);
		}).catch(()=>{});
	},

	/* 【M5】重加载页【TODO】 */
	reloadPage( page ){
		let categoryId = this.curSelect.categoryId;
		/* 1.查询品牌列表【带关联标记】 */
		this.queryList(categoryId, page);
	},

	/* 【M6】复选框的变化处理代码【TODO】
	items 就是我们选中的内容。
	*/
	handleSelectChange( items ){
		this.selectItems = items;
	},

	/* 【M7】关联品牌【TODO】 */
	associate(node, data){
		/* 1.操作类型:查询品牌列表. */
		this.opType = "brandList";
		/* 2.保存当前选中的类别信息。 */
		this.curSelect = {
			categoryId: data.id,
			categoryName: data.categoryName,
			deleteIds: [],
			brands: []
		};
		/* 3.执行品牌查询。 */
		this.queryList(data.id, 1);
	},

	/* 【M8】搜索品牌列表(带关联标记的)【TODO】 */
	doSearch(){
		let cID = this.curSelect.categoryId;
		this.queryList(cID, 1);
	},

	/* 【M9】查询列表。【TODO】 */
	queryList( categoryId, page ){
		this.curPage = page;
		/* 1.先设置类别 ID, 根据这个类别找到与它相关联的品牌
		需要标识一个关联状态。(打标记) */
		this.searchForm.categoryId = categoryId;
		/* 2.抓取 SearchForm 其它参数。 */
		let param = pickForm(this.searchForm);
		/* 3.调用 API 方法执行查询。
		再调用 processList() 来处理列表。
		*/
		listByCategory(this.curPage, this.pageSize, param)
		.then( this.processList );
	},

	/* 【M10】processList()【TODO】 */
	processList( resp ){
		let PREFIX = "http://localhost:8090/mall-sys"+
					"/Brand/showImg";
		this.brandList = resp.data;    /* 赋值列表. */
		this.totalCount = resp.total;  /* 赋值总记录数. */
		this.selectItems = [];
		/* 1.迭代列表, 做细节处理. */
		this.iterateList( PREFIX, this.brandList );
		/* 2.设置表格前面复选框选中. */
		this.setTableSelected( this.brandList );
	},

	/* 【M10.2】iterateList()【TODO】 */
	iterateList(PREFIX, list){
		this.curSelect.deleteIds = [];
		list.forEach(
			BD=>{
				BD.logoUrl = `${PREFIX}/${BD.logoName}`;
				BD.createDate = formatDate(BD.createDate);
				/* 1.保存当前页所有的 ID。 */
				this.curSelect.deleteIds.push( BD.id );
				if( BD.isAssociate==1 ){
					/* 2.打标记的数据项, 保存到已选中。 */
					this.selectItems.push( BD );
				}
			}
		);
	},

	/* 【M11】设置表格行选中方法。【TODO】
	nextTick 是 Vue 中用于在下次 DOM 更新循环结束之后
	执行延迟回调的 API.
	主要用来在【修改数据后】立即获取更新后的【DOM 元素】
	Vue 更新 DOM 是【异步】的, 【数据变了】页面不会【马上变】,
	直接操作 DOM 可能拿到【旧值】, 用它能确保拿到最新的【DOM】
	*/
	setTableSelected( brandList ){
		this.$nextTick(
			()=>{
				brandList.forEach(
					(brand)=>{
						let table = this.$refs.brandTable;
						let status = (brand.isAssociate==1)
							? true : false;
						table.toggleRowSelection(brand, status);
					}
				);
			}
		);
	},

	/* 【M12】确认选择项。【TODO】 */
	confirmSelectItems(){
		let SEL_TEMP = this.brandList.filter(
			(BD)=>{
				for(let j=0; j<this.selectItems.length; j++){
					if( BD.id==this.selectItems[j].id ){
						return true;
					}
				}
				return false;
			}
		);
		this.selectItems = SEL_TEMP;
	},

	/* 【M13】提交选择。【TODO】 */
	submitSelection(){
		this.confirmSelectItems();
		let categoryId = this.curSelect.categoryId;
		let categoryName = this.curSelect.categoryName;
		/* 1.从列表中取你想要的属性, 重新生成对象数组。 */
		let selBrands = this.selectItems.map(
			(BD)=>{
				return {id: BD.id, brandName:BD.brandName}
			}
		);
		/* 2.把已经选中的品牌, 保存到表单。 */
		this.curSelect.brands = selBrands;
		/* 3.调用 API 方法提交数据。 */
		associateBrand( this.curSelect )
		.then(
			resp=>{
				this.$message(`类别【${categoryName}】关联品牌成功。`);
				this.queryList( categoryId, 1 );
			}
		);
	},

	/* 【M14】保存类别。【TODO】 */
	saveCategory(){
		if( this.opType=="add" ){
			this.doAddCategory();
		}else{
			this.doUpdateCategory();
		}
	},

	/* 【M15】添加类别的【数据库保存】。【TODO】 */
	doAddCategory(){
		let category = this.categoryForm;
		let cName = category.categoryName;
		category.icons = "icons";
		/* 2.调用 API 方法保存. */
		addCategory( category )
		.then(
			resp=>{
				this.onSaveSuccess("添加",cName);
			}
		);
	},

	/* 【M15.2】保存成功后的数据数据复位。【TODO】 */
	onSaveSuccess(saveType, categoryName){
		/* 1.重新加载类别树. */
		this.loadCategoryTree();
		this.$message(`${saveType}【${categoryName}】类别成功。`);
		/* 2.表单重置. */
		this.categoryForm = {};
		this.opType = "";
		this.showFormBox = false;
	},

	/* 【M16】doUpdateCategory【数据库保存】。【TODO】 */
	doUpdateCategory(){
		let category = this.categoryForm;
		let cName = category.categoryName;
		category.icons = "icons";
		/* 2.调用 API 方法保存. */
		updateCategory( category )
		.then(
			resp=>{
				this.onSaveSuccess("更新",cName);
			}
		);
	},

	/* 【M17】加载类别的树形列表。【TODO】 */
	loadCategoryTree(){
		getCategoryList()
		.then(
			resp=>{
				this.categoryList = resp.data;
				if( this.categoryList
				&& this.categoryList.length>0 ){
					/* 1.设置叶子标记. */
					this.setLeap(this.categoryList, 1);
					/* 2.给每类别上设置父节点名字。 */
					this.setParentName(
						this.categoryList, "顶层类别"
					);
				}
			}
		);
	},

	/* 【M18】设置父类别的名称。【TODO】 */
	setParentName( children, parName ){
		let size = children.length;
		for( let i=0; i<size; i++ ){
			let C = children[i];           //当前类别(子类别)
			let cName = C.categoryName;
			let nextChild = C.children;    //下一级子类别
			C.parentName = parName;        //关键点
			if( nextChild && nextChild.length>0 ){
				this.setParentName(nextChild, cName);
			}
		}
	},

	/* setLeap() 标记叶子节点 */
	setLeap(childList, level){
		childList.forEach(
			(D)=>{
				D.level = level;
				let children = D.children;
				if( !children || children.length==0 ){
					D.isLeap = true;
				}else{
					D.isLeap = false;
					this.setLeap(children, level+1);
				}
			}
		);
	},

	/* 关闭对话框 */
	handleClose(){
		this.showFormBox = false;
	}
  },

  /* {3}生命周期方法 */
  created(){
	 this.loadCategoryTree();
  }

}
</script>

<style>
.custom-tree-node{
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: space-between;
	font-size: 14px;
	padding-right: 8px;
}
</style>
