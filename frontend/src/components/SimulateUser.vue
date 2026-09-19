<template>
  <div class="sim-page">
    <!-- ═══ 左：模拟商城前台 ═══ -->
    <div class="shop-pane">
      <div class="shop-top">
        <div class="shop-title">
          <span class="shop-dot"></span>
          模拟商城前台
        </div>
        <el-input
          v-model="searchKeyword"
          size="small"
          placeholder="输入关键词模拟搜索，回车提交"
          class="shop-search"
          @keyup.enter.native="handleSearch"
          clearable>
        </el-input>
        <el-tag size="small" type="info">{{ userId }}</el-tag>
        <el-tag size="small">{{ deviceType }} · {{ city }}</el-tag>
      </div>

      <div class="goods-grid">
        <div v-for="g in goodsList" :key="g.id" class="goods-card" :class="{flash: g._flash}">
          <div class="goods-img" :style="g.mainImage ? {} : {background: grad(g.id)}">
            <img v-if="g.mainImage" class="goods-img-real" :src="coverUrl(g.mainImage)" />
            <span v-else>{{ g.goodsName.charAt(0) }}</span>
          </div>
          <div class="goods-name" :title="g.goodsName">{{ g.goodsName }}</div>
          <div class="goods-price">￥{{ g.price }}</div>
          <div class="goods-btns">
            <el-button size="mini" @click="handleView(g)">浏览</el-button>
            <el-button size="mini" :type="isFav(g) ? 'success' : 'warning'" @click="handleFav(g)">{{ isFav(g) ? '已收藏' : '收藏' }}</el-button>
            <el-button size="mini" type="primary" @click="handleAddCart(g)">加购</el-button>
            <el-button size="mini" type="success" @click="handleBuy(g)">购买</el-button>
          </div>
        </div>
        <div v-if="goodsList.length === 0" class="goods-empty">商品加载中…</div>
      </div>

      <div class="cart-bar">
        <span>🛒 购物车：<b>{{ cartCount }}</b> 件</span>
        <el-button size="mini" type="text" @click="clearCart">清空购物车</el-button>
      </div>
    </div>

    <!-- ═══ 右：行为日志终端 ═══ -->
    <div class="term-pane">
      <div class="term-title">
        <div class="term-dots">
          <i style="background:#ff5f57"></i>
          <i style="background:#febc2e"></i>
          <i style="background:#28c840"></i>
        </div>
        <span class="term-name">user-behavior.log — live</span>
        <div class="term-ctrl">
          <el-button size="mini" @click="clearLogs">清空</el-button>
          <el-button size="mini" @click="exportLogs">导出</el-button>
          <span class="term-switch">自动模拟</span>
          <el-switch v-model="autoMode" @change="toggleAuto"></el-switch>
        </div>
      </div>
      <div class="term-body" ref="logBox">
        <div v-for="(l, i) in logs" :key="i" class="log-line" :style="{color: levelColor(l.level)}">
          <span class="log-time">{{ l.time }}</span>
          <span class="log-level">[{{ l.level }}]</span>
          <span>{{ l.text }}</span>
        </div>
        <div class="cursor">▌</div>
      </div>
    </div>
  </div>
</template>

<script>
import { getGoodsList } from '@/api/pms_goods.js'
import { goodsCoverUrl } from '@/utils/imageUrl'

var DEMO_GOODS = [
  { id: 9001, goodsName: '旗舰智能手机 Pro Max', price: 5999 },
  { id: 9002, goodsName: '轻薄笔记本电脑 14寸', price: 6999 },
  { id: 9003, goodsName: '无线降噪耳机', price: 1299 },
  { id: 9004, goodsName: '智能手表运动版', price: 1599 },
  { id: 9005, goodsName: '4K 高清投影仪', price: 3299 },
  { id: 9006, goodsName: '机械键盘 87键', price: 459 },
  { id: 9007, goodsName: '人体工学办公椅', price: 899 },
  { id: 9008, goodsName: '便携咖啡机', price: 699 }
]

var GRADS = [
  'linear-gradient(135deg,#ff6b6b,#feca57)',
  'linear-gradient(135deg,#48dbfb,#0abde3)',
  'linear-gradient(135deg,#1dd1a1,#10ac84)',
  'linear-gradient(135deg,#f368e0,#ff9ff3)',
  'linear-gradient(135deg,#feca57,#ff9f43)',
  'linear-gradient(135deg,#54a0ff,#2e86de)'
]

export default {
  name: 'SimulateUser',
  data() {
    return {
      goodsList: [],
      searchKeyword: '',
      userId: 'u_' + Math.floor(10000 + Math.random() * 89999),
      sessionId: 's_' + Date.now().toString(36),
      deviceType: ['iPhone15', 'Android14', 'WindowsPC', 'MacBook'][Math.floor(Math.random() * 4)],
      city: ['北京', '上海', '深圳', '杭州', '广州'][Math.floor(Math.random() * 5)],
      favorites: [],
      cart: {},
      logs: [],
      autoMode: false,
      autoTimer: null
    }
  },
  computed: {
    cartCount() {
      var n = 0
      for (var k in this.cart) { n += this.cart[k] }
      return n
    }
  },
  created() {
    this.loadGoods()
  },
  beforeDestroy() {
    if (this.autoTimer) { clearInterval(this.autoTimer) }
    this.autoTimer = null
  },
  methods: {
    coverUrl( fileName ){
      return goodsCoverUrl( fileName );
    },
    grad(id) {
      return GRADS[id % GRADS.length]
    },
    isFav(g) {
      return this.favorites.indexOf(g.id) > -1
    },
    levelColor(level) {
      var map = {
        boot: '#58a6ff',
        view: '#8b949e',
        search: '#d2a8ff',
        fav: '#e3b341',
        cart: '#39d2c0',
        buy: '#7ee787',
        pay: '#3fb950'
      }
      return map[level] || '#c9d1d9'
    },
    loadGoods() {
      getGoodsList({ page: 1, limit: 12, publishStatus: 1 }).then((resp) => {
        var list = (resp && resp.data && resp.data.goods) ? resp.data.goods : (resp && resp.data || [])
        this.goodsList = list.slice(0, 12).map(function (item) {
          return {
            id: item.id,
            goodsName: item.goodsName || item.goods_name || ('商品' + item.id),
            price: item.price,
            mainImage: item.mainImage || item.main_image || ''
          }
        })
        this.bootLogs()
      }).catch(() => {
        this.goodsList = DEMO_GOODS
        this.bootLogs()
      })
      // 兜底：接口慢/挂时 3 秒后仍为空则用演示数据
      setTimeout(() => {
        if (this.goodsList.length === 0) {
          this.goodsList = DEMO_GOODS
          this.bootLogs()
        }
      }, 3000)
    },
    bootLogs() {
      if (this.logs.length > 0) { return }
      this.pushLog('boot', '会话建立 session=' + this.sessionId + ' device=' + this.deviceType + ' city=' + this.city)
      this.pushLog('boot', '用户 ' + this.userId + ' 进入商城前台')
      this.pushLog('boot', '加载推荐商品 ' + this.goodsList.length + ' 件')
    },
    fmtTime(d) {
      var h = d.getHours()
      var m = d.getMinutes()
      var s = d.getSeconds()
      return (h < 10 ? '0' + h : '' + h) + ':' + (m < 10 ? '0' + m : '' + m) + ':' + (s < 10 ? '0' + s : '' + s)
    },
    pushLog(level, text) {
      this.logs.push({ time: this.fmtTime(new Date()), level: level, text: text })
      if (this.logs.length > 500) { this.logs.shift() }
      this.$nextTick(() => {
        if (this.$refs.logBox) {
          this.$refs.logBox.scrollTop = this.$refs.logBox.scrollHeight
        }
      })
    },
    handleView(g) {
      this.pushLog('view', '浏览商品 ' + g.goodsName + ' (id=' + g.id + ') ￥' + g.price)
      var self = this
      g._flash = true
      setTimeout(() => { g._flash = false }, 200)
    },
    handleFav(g) {
      var idx = this.favorites.indexOf(g.id)
      if (idx > -1) {
        this.favorites.splice(idx, 1)
        this.pushLog('fav', '取消收藏 ' + g.goodsName)
      } else {
        this.favorites.push(g.id)
        this.pushLog('fav', '收藏商品 ' + g.goodsName)
      }
    },
    handleAddCart(g) {
      this.cart[g.id] = (this.cart[g.id] || 0) + 1
      // 触发响应式更新
      this.cart = Object.assign({}, this.cart)
      this.pushLog('cart', '加入购物车 ' + g.goodsName + ' 数量=' + this.cart[g.id] + ' 单价￥' + g.price)
    },
    handleBuy(g) {
      this.pushLog('buy', '提交订单 ' + g.goodsName + ' ￥' + g.price)
      setTimeout(() => {
        this.pushLog('pay', '支付成功 订单金额￥' + g.price)
      }, 400)
    },
    handleSearch() {
      var kw = (this.searchKeyword || '').trim()
      if (!kw) { return }
      this.pushLog('search', '搜索关键词「' + kw + '」')
      this.searchKeyword = ''
    },
    clearCart() {
      this.cart = {}
      this.pushLog('cart', '清空购物车')
    },
    clearLogs() {
      this.logs = []
    },
    exportLogs() {
      var lines = []
      for (var i = 0; i < this.logs.length; i++) {
        var l = this.logs[i]
        lines.push(l.time + ' ' + l.level + ' ' + l.text)
      }
      var blob = new Blob([lines.join('\n')], { type: 'text/plain;charset=utf-8' })
      var a = document.createElement('a')
      a.href = URL.createObjectURL(blob)
      a.download = 'user-behavior.log.txt'
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(a.href)
    },
    toggleAuto(on) {
      if (on) {
        var self = this
        this.autoTimer = setInterval(() => {
          if (self.goodsList.length === 0) { return }
          var g = self.goodsList[Math.floor(Math.random() * self.goodsList.length)]
          var acts = ['view', 'cart', 'fav']
          var act = acts[Math.floor(Math.random() * acts.length)]
          if (act === 'view') { self.handleView(g) }
          else if (act === 'cart') { self.handleAddCart(g) }
          else { self.handleFav(g) }
        }, 900)
        this.pushLog('boot', '自动模拟已开启')
      } else {
        if (this.autoTimer) { clearInterval(this.autoTimer) }
        this.autoTimer = null
        this.pushLog('boot', '自动模拟已关闭')
      }
    }
  }
}
</script>

<style scoped>
.sim-page {
  display: grid;
  grid-template-columns: 1fr 560px;
  gap: 16px;
  height: calc(100vh - 140px);
  overflow: hidden;
}

/* ── 左侧商城 ── */
.shop-pane {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--panel, #fff);
  border-radius: 12px;
  border: 1px solid var(--line, #e5e7eb);
}
.shop-top {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--line, #e5e7eb);
  flex-shrink: 0;
}
.shop-title {
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}
.shop-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--dop-cyan, #39d2c0);
}
.shop-search {
  width: 240px;
  margin-left: auto;
}
.goods-grid {
  flex: 1;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 16px;
  align-content: start;
}
.goods-card {
  border: 1px solid var(--line, #e5e7eb);
  border-radius: 10px;
  padding: 12px;
  transition: box-shadow .15s, background .15s;
}
.goods-card.flash {
  background: rgba(57, 210, 192, .15);
  box-shadow: 0 0 0 2px var(--dop-cyan, #39d2c0);
}
.goods-img {
  width: 100%;
  height: 90px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  overflow: hidden;
}
.goods-img-real {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.goods-name {
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}
.goods-price {
  color: #e5484d;
  font-weight: 700;
  margin-bottom: 8px;
}
.goods-btns {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
.goods-empty {
  grid-column: 1 / -1;
  text-align: center;
  color: var(--muted, #999);
  padding: 40px 0;
}
.cart-bar {
  border-top: 1px solid var(--line, #e5e7eb);
  padding: 10px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}

/* ── 右侧终端 ── */
.term-pane {
  display: flex;
  flex-direction: column;
  background: #0d1117;
  border-radius: 12px;
  overflow: hidden;
}
.term-title {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: #161b22;
  border-bottom: 1px solid #30363d;
  flex-shrink: 0;
}
.term-dots i {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 6px;
}
.term-name {
  color: #8b949e;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  flex: 1;
  white-space: nowrap;
}
.term-ctrl {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}
.term-switch {
  color: #8b949e;
  font-size: 12px;
}
.term-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
  line-height: 1.7;
}
.log-line {
  word-break: break-all;
}
.log-time {
  color: #484f58;
  margin-right: 8px;
}
.log-level {
  margin-right: 8px;
}
.cursor {
  color: #39d2c0;
  animation: blink 1s steps(1) infinite;
  display: inline-block;
}
@keyframes blink {
  0%, 49% { opacity: 1; }
  50%, 100% { opacity: 0; }
}
</style>
