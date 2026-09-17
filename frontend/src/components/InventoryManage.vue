<template>
  <div>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>库存管理</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 顶部统计卡片 -->
      <el-row :gutter="16" class="stat-row">
        <el-col :span="6" v-for="item in statCards" :key="item.label">
          <div class="stat-card" :style="{borderLeftColor: item.color}">
            <div class="stat-label">{{ item.label }}</div>
            <div class="stat-value" :style="{color: item.color}">{{ item.value }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 筛选区 -->
      <el-form :inline="true" :model="searchForm" style="margin-top:16px;">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入商品名称" style="width:160px;" clearable></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="全部分类" clearable style="width:140px;">
            <el-option v-for="cat in categoryOptions" :key="cat.id" :label="cat.categoryName" :value="cat.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="品牌">
          <el-select v-model="searchForm.brandId" placeholder="全部品牌" clearable style="width:140px;">
            <el-option v-for="br in brandOptions" :key="br.id" :label="br.brandName" :value="br.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="searchForm.stockStatus" placeholder="全部" clearable style="width:120px;">
            <el-option label="正常" value="normal"></el-option>
            <el-option label="低库存" value="low"></el-option>
            <el-option label="缺货" value="out"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doSearch">搜索</el-button>
          <el-button @click="doReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" style="width:100%" border @expand-change="handleExpand">
        <el-table-column type="expand" width="50">
          <template slot-scope="scope">
            <div style="padding:10px 30px;">
              <div style="margin-bottom:8px; font-weight:bold; color:#606266;">SKU 库存明细</div>
              <el-table :data="scope.row.skuList" size="mini" border>
                <el-table-column prop="skuId" label="SKU ID" width="80"></el-table-column>
                <el-table-column prop="skuName" label="SKU名称" width="180"></el-table-column>
                <el-table-column label="价格" width="100">
                  <template slot-scope="s">
                    <span style="color:#f56c6c;">￥{{ s.row.price }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="库存" width="100">
                  <template slot-scope="s">
                    <el-input-number v-model="s.row.stock" :min="0" size="mini" style="width:90px;"></el-input-number>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="140">
                  <template slot-scope="s">
                    <el-button type="text" size="mini" @click="saveSkuStock(s.row)">保存</el-button>
                    <el-button type="text" size="mini" @click="openSkuStocktake(s.row)">盘点</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="goodsId" label="商品ID" width="80"></el-table-column>
        <el-table-column prop="goodsName" label="商品名称" width="180" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="goodsSn" label="商品编号" width="120"></el-table-column>
        <el-table-column prop="skuCount" label="SKU数" width="70" align="center"></el-table-column>
        <el-table-column label="总库存" width="100" align="center">
          <template slot-scope="scope">
            <span :style="{color: getStockColor(scope.row.stock), fontWeight:'bold'}">{{ scope.row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="saleCount" label="已售" width="80" align="center"></el-table-column>
        <el-table-column label="库存状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStockTag(scope.row.stock)" size="mini">
              {{ getStockLabel(scope.row.stock) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="库存价值" width="120" align="right">
          <template slot-scope="scope">
            <span style="color:#E6A23C;">￥{{ getStockValue(scope.row) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="openAdjust(scope.row)">调整</el-button>
            <el-button type="text" size="mini" @click="openStocktake(scope.row)">盘点</el-button>
            <el-button type="text" size="mini" @click="openLog(scope.row)">流水</el-button>
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
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        style="margin-top:12px; text-align:right;">
      </el-pagination>
    </el-card>

    <!-- 调整库存弹窗 -->
    <el-dialog title="调整库存" :visible.sync="adjustVisible" width="400px" :close-on-click-modal="false">
      <el-form :model="adjustForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ adjustForm.goodsName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ adjustForm.oldStock }}</span>
        </el-form-item>
        <el-form-item label="新库存" required>
          <el-input-number v-model="adjustForm.stock" :min="0" :step="10" style="width:100%;"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="adjustVisible=false">取消</el-button>
        <el-button type="primary" @click="doAdjust">确认调整</el-button>
      </span>
    </el-dialog>

    <!-- 库存流水弹窗 -->
    <el-dialog title="库存流水" :visible.sync="logVisible" width="800px" :close-on-click-modal="false">
      <el-form :inline="true" size="small" style="margin-bottom:8px;">
        <el-form-item label="变动类型">
          <el-select v-model="logFilter.changeType" placeholder="全部" clearable style="width:140px;" @change="loadLogList">
            <el-option label="下单锁定" :value="1"></el-option>
            <el-option label="发货扣减" :value="2"></el-option>
            <el-option label="取消回滚" :value="3"></el-option>
            <el-option label="退货回滚" :value="4"></el-option>
            <el-option label="手动调整" :value="5"></el-option>
            <el-option label="库存盘点" :value="6"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-table :data="logList" border size="small">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="skuId" label="SKU ID" width="80">
          <template slot-scope="scope">{{ scope.row.skuId || '-' }}</template>
        </el-table-column>
        <el-table-column label="变动类型" width="100">
          <template slot-scope="scope">{{ getChangeTypeName(scope.row.changeType) }}</template>
        </el-table-column>
        <el-table-column label="变动数量" width="90">
          <template slot-scope="scope">
            <span :style="{color: scope.row.changeQty > 0 ? '#67C23A' : '#f56c6c'}">
              {{ scope.row.changeQty > 0 ? '+' : '' }}{{ scope.row.changeQty }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="beforeStock" label="变动前" width="80"></el-table-column>
        <el-table-column prop="afterStock" label="变动后" width="80"></el-table-column>
        <el-table-column prop="orderNo" label="关联订单" width="140">
          <template slot-scope="scope">{{ scope.row.orderNo || '-' }}</template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="80"></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="时间" width="150"></el-table-column>
      </el-table>
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="logTotal"
        :current-page="logPage"
        :page-size="10"
        @current-change="handleLogPage"
        style="margin-top:12px; text-align:right;">
      </el-pagination>
    </el-dialog>

    <!-- 库存盘点弹窗 -->
    <el-dialog title="库存盘点" :visible.sync="stocktakeVisible" width="420px" :close-on-click-modal="false">
      <el-form :model="stocktakeForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ stocktakeForm.goodsName }}</span>
        </el-form-item>
        <el-form-item label="SKU名称" v-if="stocktakeForm.skuName">
          <span>{{ stocktakeForm.skuName }}</span>
        </el-form-item>
        <el-form-item label="系统库存">
          <span style="color:#999;">{{ stocktakeForm.oldStock }}</span>
        </el-form-item>
        <el-form-item label="实际盘点" required>
          <el-input-number v-model="stocktakeForm.actualStock" :min="0" :step="1" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="差异">
          <span :style="{color: getDiffColor()}">{{ getDiffText() }}</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stocktakeForm.remark" placeholder="选填，如：月度盘点"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="stocktakeVisible=false">取消</el-button>
        <el-button type="primary" @click="doStocktake">确认盘点</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  statistics,
  list,
  skuDetail,
  adjustStock,
  adjustSkuStock,
  inventoryLog,
  stocktake
} from '@/api/pms_inventory.js'
import { getCategoryList } from '@/api/pms_category.js'
import { list as getBrandList } from '@/api/pms_brand.js'

export default {
  name: 'InventoryManage',
  data() {
    return {
      /* 统计 */
      statData: { totalGoods: 0, totalStock: 0, lowStockCount: 0, totalValue: 0 },

      /* 搜索 */
      searchForm: {
        keyword: '',
        categoryId: null,
        brandId: null,
        stockStatus: ''
      },
      categoryOptions: [],
      brandOptions: [],

      /* 表格 */
      tableData: [],
      totalCount: 0,
      curPage: 1,
      pageSize: 10,

      /* 弹窗 */
      adjustVisible: false,
      adjustForm: { goodsId: null, goodsName: '', oldStock: 0, stock: 0 },

      /* 流水弹窗 */
      logVisible: false,
      logList: [],
      logTotal: 0,
      logPage: 1,
      logFilter: { goodsId: null, skuId: null, changeType: null },
      /* 盘点弹窗 */
      stocktakeVisible: false,
      stocktakeForm: { goodsId: null, skuId: null, goodsName: '', skuName: '', oldStock: 0, actualStock: 0, remark: '' }
    }
  },
  computed: {
    statCards() {
      return [
        { label: '商品总数', value: this.statData.totalGoods, color: '#409EFF' },
        { label: '库存总量', value: this.statData.totalStock, color: '#67C23A' },
        { label: '低库存预警', value: this.statData.lowStockCount, color: '#f56c6c' },
        { label: '库存总价值', value: '￥' + this.formatValue(this.statData.totalValue), color: '#E6A23C' }
      ]
    }
  },
  methods: {
    /* 格式化金额 */
    formatValue(val) {
      if (!val) return '0.00'
      return Number(val).toFixed(2)
    },

    /* 加载统计 */
    loadStatistics() {
      statistics().then(resp => {
        this.statData = resp.data || this.statData
      })
    },

    /* 加载分类/品牌 */
    loadOptions() {
      getCategoryList().then(resp => {
        var options = []
        this.flattenCategory(resp.data, 0, options)
        this.categoryOptions = options
      })
      getBrandList(1, 100, {}).then(resp => {
        this.brandOptions = resp.data
      })
    },
    flattenCategory(nodes, level, out) {
      if (!nodes) return
      nodes.forEach(node => {
        out.push({
          id: node.id,
          categoryName: '　'.repeat(level) + node.categoryName
        })
        this.flattenCategory(node.children, level + 1, out)
      })
    },

    /* 搜索 */
    doSearch() {
      this.curPage = 1
      this.loadList()
    },

    /* 重置 */
    doReset() {
      this.searchForm = {
        keyword: '',
        categoryId: null,
        brandId: null,
        stockStatus: ''
      }
      this.curPage = 1
      this.loadList()
    },

    /* 加载列表 */
    loadList() {
      var params = {
        keyword: this.searchForm.keyword || null,
        categoryId: this.searchForm.categoryId,
        brandId: this.searchForm.brandId,
        stockStatus: this.searchForm.stockStatus || null
      }
      list(this.curPage, this.pageSize, params).then(resp => {
        var data = resp.data || {}
        this.tableData = data.data || []
        this.totalCount = data.total || 0
        /* 每行初始化skuList为空，展开时加载 */
        this.tableData.forEach(function(row) {
          row.skuList = []
          row.skuLoaded = false
        })
      })
    },

    /* 分页 */
    handlePageChange(page) {
      this.curPage = page
      this.loadList()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.curPage = 1
      this.loadList()
    },

    /* 库存状态 */
    getStockLabel(stock) {
      if (stock == 0 || stock == null) return '缺货'
      if (stock < 20) return '低库存'
      return '正常'
    },
    getStockTag(stock) {
      if (stock == 0 || stock == null) return 'danger'
      if (stock < 20) return 'warning'
      return 'success'
    },
    getStockColor(stock) {
      if (stock == 0 || stock == null) return '#f56c6c'
      if (stock < 20) return '#E6A23C'
      return '#67C23A'
    },

    /* 库存价值 */
    getStockValue(row) {
      var price = row.price || 0
      var stock = row.stock || 0
      return (price * stock).toFixed(2)
    },

    /* 展开行时加载SKU明细 */
    loadSkuDetail(row) {
      if (row.skuLoaded) return
      skuDetail(row.goodsId).then(resp => {
        row.skuList = resp.data || []
        row.skuLoaded = true
      })
    },

    /* 展开行时加载SKU明细 */
    handleExpand(row, expandedRows) {
      if (expandedRows.indexOf(row) > -1 && !row.skuLoaded) {
        this.loadSkuDetail(row)
      }
    },

    /* 保存SKU库存 */
    saveSkuStock(skuRow) {
      adjustSkuStock(skuRow.skuId, skuRow.stock).then(() => {
        this.$message.success('SKU库存已更新')
        this.loadStatistics()
      })
    },

    /* 打开调整库存弹窗 */
    openAdjust(row) {
      this.adjustForm = {
        goodsId: row.goodsId,
        goodsName: row.goodsName,
        oldStock: row.stock,
        stock: row.stock
      }
      this.adjustVisible = true
    },

    /* 确认调整 */
    doAdjust() {
      adjustStock(this.adjustForm.goodsId, this.adjustForm.stock).then(() => {
        this.$message.success('库存调整成功')
        this.adjustVisible = false
        this.loadList()
        this.loadStatistics()
      })
    },

    /* 变动类型名称 */
    getChangeTypeName(type) {
      switch (type) {
        case 1: return '下单锁定'
        case 2: return '发货扣减'
        case 3: return '取消回滚'
        case 4: return '退货回滚'
        case 5: return '手动调整'
        case 6: return '库存盘点'
        default: return '未知'
      }
    },

    /* 打开流水弹窗 */
    openLog(row) {
      this.logFilter.goodsId = row.goodsId
      this.logFilter.skuId = null
      this.logFilter.changeType = null
      this.logPage = 1
      this.logVisible = true
      this.loadLogList()
    },

    /* 加载流水列表 */
    loadLogList() {
      inventoryLog(this.logPage, 10, this.logFilter).then(resp => {
        this.logList = resp.data || []
        this.logTotal = resp.total || 0
      })
    },

    /* 流水分页 */
    handleLogPage(page) {
      this.logPage = page
      this.loadLogList()
    },

    /* 打开商品盘点 */
    openStocktake(row) {
      this.stocktakeForm = {
        goodsId: row.goodsId,
        skuId: null,
        goodsName: row.goodsName,
        skuName: '',
        oldStock: row.stock,
        actualStock: row.stock,
        remark: ''
      }
      this.stocktakeVisible = true
    },

    /* 打开SKU盘点 */
    openSkuStocktake(skuRow) {
      this.stocktakeForm = {
        goodsId: skuRow.goodsId,
        skuId: skuRow.skuId,
        goodsName: '',
        skuName: skuRow.skuName,
        oldStock: skuRow.stock,
        actualStock: skuRow.stock,
        remark: ''
      }
      this.stocktakeVisible = true
    },

    /* 盘点差异颜色 */
    getDiffColor() {
      var diff = this.stocktakeForm.actualStock - this.stocktakeForm.oldStock
      if (diff > 0) return '#67C23A'
      if (diff < 0) return '#f56c6c'
      return '#909399'
    },
    getDiffText() {
      var diff = this.stocktakeForm.actualStock - this.stocktakeForm.oldStock
      if (diff > 0) return '+' + diff
      if (diff < 0) return '' + diff
      return '0（无差异）'
    },

    /* 确认盘点 */
    doStocktake() {
      stocktake(this.stocktakeForm.goodsId, this.stocktakeForm.skuId, this.stocktakeForm.actualStock, this.stocktakeForm.remark).then(() => {
        this.$message.success('盘点完成')
        this.stocktakeVisible = false
        this.loadList()
        this.loadStatistics()
      })
    }
  },
  created() {
    this.loadStatistics()
    this.loadOptions()
    this.loadList()
  }
}
</script>

<style scoped>
.box-card { margin: 12px; }
.stat-row { margin-bottom: 8px; }
.stat-card {
  background: #fafafa;
  border: 1px solid #ebeef5;
  border-left: 4px solid #409EFF;
  border-radius: 4px;
  padding: 16px;
  text-align: center;
}
.stat-label { font-size: 13px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 26px; font-weight: bold; }
</style>
