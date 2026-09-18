# 执行席方案 — 电商后台 UI 改版落地

> 生成时间：2026-09-17
> 指挥席：Doubao
> 执行席：atomcode CLI / 人工
> 原型参考：`D:\vue-space\mypro5\ui-design-runs\你是指挥席先用douba-20260917-234816-a10d\console\index.html`
> 原型已通过 doubao-ui-design 真值门验证（14/16 通过，BLOCK 0）

---

## 一、改动概述

将电商后台从**顶端 table 导航**重构为**左侧固定导航 + 顶部栏 + 内容区**的经典 B 端布局，同时应用**多巴胺配色**，当前页导航项为**深蓝色**。

### 改动前
- 导航：`<table id="NAV">` 横向排列，黑色背景 `#222`，黄色文字
- 布局：导航在顶部，`<router-view/>` 在下方
- 配色：黑底黄字，无品牌色

### 改动后
- 导航：左侧固定侧边栏 240px，白色背景，分组导航
- 布局：左侧导航 + 顶部栏（标题/搜索/用户）+ 内容区
- 配色：多巴胺多彩（每个导航项图标不同色）+ 当前页深蓝 `#1E3A8A` + 暖调浅底 `#F5F3EF`

---

## 二、涉及文件

| 文件 | 改动类型 | 说明 |
|------|---------|------|
| `src/App.vue` | **重写** | 整体布局重构：顶端 table → 左侧导航 + 顶部栏 |
| `src/main.js` | 追加 | 引入全局 CSS 变量（如已有全局样式文件则改那个） |
| `src/App.vue` 的 `<style>` | 重写 | 旧的 `#NAV` 样式全部删除，替换为新布局样式 |

> 注意：**不需要修改任何子组件**（User.vue、Brand.vue 等），它们通过 `<router-view/>` 渲染，布局变化对它们透明。

---

## 三、全局 CSS 变量（定义在 App.vue `<style>` 顶部）

```css
:root{
  /* 基底 */
  --canvas:#F5F3EF;        /* 页面底：暖调浅灰 */
  --panel:#FFFFFF;         /* 卡片/侧栏底 */
  --panel-2:#FAF8F5;       /* 内嵌底（搜索框/表头） */
  --line:rgba(26,26,46,.07); /* 发丝线 */
  /* 墨 */
  --ink:#1A1A2E;
  --ink-2:#4A4A5A;
  --muted:#8B8B9A;
  /* 品牌：深蓝（当前页/主操作） */
  --accent:#1E3A8A;
  --accent-soft:rgba(30,58,138,.10);
  --on-accent:#ffffff;
  /* 多巴胺色板（导航图标/KPI/图表用） */
  --dop-orange:#FF8C42;
  --dop-green:#6BCB77;
  --dop-pink:#FF6B9D;
  --dop-purple:#9B5DE5;
  --dop-cyan:#00BBF9;
  --dop-yellow:#FFD93D;
  --dop-blue:#4D96FF;
  /* 语义 */
  --ok:#059669; --ok-ink:#036043;
  --warn:#D97706; --warn-ink:#8A4B04;
  --bad:#DC2626; --bad-ink:#991B1B;
  /* 几何 */
  --r:10px;
  --r-lg:14px;
  --side:240px;
}
```

---

## 四、App.vue 具体改动

### 4.1 `<template>` 重写

**删除**：整个 `<div v-if="isLoggedIn">` 内的 `<table id="NAV">` 及其所有 `<td>`。

**替换为**：

```html
<template>
  <div id="app">
    <!-- 登录页不显示导航 -->
    <div v-if="isLoggedIn && $route.path !== '/login'" class="app-layout">
      <!-- ═══ 左侧导航栏 ═══ -->
      <nav class="side">
        <div class="brand">
          <div class="brand-logo">商</div>
          <span class="brand-text">电商后台</span>
        </div>

        <div class="nav-section">主导航</div>
        <router-link to="/dashBoard" class="nav-item" :class="{on: P==1}" @click.native="select(1)">
          <svg class="ic" style="color:var(--dop-blue)" viewBox="0 0 256 256" fill="currentColor"><path d="M219.31,108.68l-80-80a16,16,0,0,0-22.62,0l-80,80A15.87,15.87,0,0,0,32,120v96a8,8,0,0,0,8,8h64a8,8,0,0,0,8-8V160h32v56a8,8,0,0,0,8,8h64a8,8,0,0,0,8-8V120A15.87,15.87,0,0,0,219.31,108.68Z"/></svg>
          <span>数据全景</span>
        </router-link>
        <router-link to="/user" class="nav-item" :class="{on: P==2}" @click.native="select(2)">
          <svg class="ic" style="color:var(--dop-cyan)" viewBox="0 0 256 256" fill="currentColor"><path d="M117.25,157.92a60,60,0,1,0-66.5,0A95.83,95.83,0,0,0,3.53,195.63a8,8,0,1,0,13.4,8.74,80,80,0,0,1,134.14,0,8,8,0,0,0,13.4-8.74A95.83,95.83,0,0,0,117.25,157.92ZM40,108a44,44,0,1,1,44,44A44.05,44.05,0,0,1,40,108Z"/></svg>
          <span>用户管理</span>
        </router-link>
        <!-- ... 其余导航项按下方导航清单逐一添加 ... -->

        <div class="nav-section">商品</div>
        <!-- 商品类别/品牌/属性分组/规格参数/销售属性/商品管理/发布商品/SKU管理 -->

        <div class="nav-section">运营</div>
        <!-- 订单管理/活动管理/会员管理/库存管理/广告设置 -->

        <div class="nav-section">数据分析</div>
        <!-- 用户行为/订单分析/评价分析/客服交流 -->

        <div class="side-footer">
          <div class="user-chip" @click="handleLogout">
            <div class="avatar">管</div>
            <div>
              <div class="user-name">管理员</div>
              <div class="user-email">退出登录</div>
            </div>
          </div>
        </div>
      </nav>

      <!-- ═══ 主区 ═══ -->
      <div class="main">
        <header class="top">
          <h1>{{ currentPageTitle }}</h1>
          <span class="sp"></span>
          <div class="search-box">
            <svg class="ic" style="width:16px;height:16px;color:var(--muted)" viewBox="0 0 256 256" fill="currentColor"><path d="M229.66,218.34,169.43,158.1a88.19,88.19,0,1,0-11.31,11.31l60.23,60.23a8,8,0,0,0,11.31-11.31ZM40,112a72,72,0,1,1,72,72A72.08,72.08,0,0,1,40,112Z"/></svg>
            <input placeholder="搜索商品、订单、会员..." />
          </div>
        </header>
        <div class="body">
          <router-view/>
        </div>
      </div>
    </div>

    <!-- 登录页全屏显示 -->
    <router-view v-else/>
  </div>
</template>
```

### 4.2 导航项完整清单（按 P 值和路由）

| P | 名称 | 路由 | 分组 | 图标色 |
|---|------|------|------|--------|
| 1 | 数据全景 | `/dashBoard` | 主导航 | `--dop-blue` |
| 2 | 用户管理 | `/user` | 主导航 | `--dop-cyan` |
| 3 | 部门管理 | `/dept` | 主导航 | `--dop-green` |
| 4 | 商品类别 | `/category` | 商品 | `--dop-orange` |
| 5 | 品牌管理 | `/brand` | 商品 | `--dop-pink` |
| 6 | 属性分组 | `/attrGroup` | 商品 | `--dop-purple` |
| 8 | 规格参数 | `/goodsAttr` | 商品 | `--dop-yellow` |
| 9 | 销售属性 | `/saleAttr` | 商品 | `--dop-blue` |
| 10 | 商品管理 | `/goodsManage` | 商品 | `--dop-cyan` |
| 11 | 发布商品 | `/publishBaseInfo` | 商品 | `--dop-green` |
| 16 | SKU管理 | `/skuManage` | 商品 | `--dop-orange` |
| 12 | 订单管理 | `/orderManage` | 运营 | `--dop-pink` |
| 17 | 活动管理 | `/couponManage` | 运营 | `--dop-purple` |
| 18 | 会员管理 | `/memberManage` | 运营 | `--dop-yellow` |
| 19 | 库存管理 | `/inventoryManage` | 运营 | `--dop-blue` |
| 20 | 广告设置 | `/advertManage` | 运营 | `--dop-cyan` |
| 21 | 用户行为 | `/analysis/behavior` | 数据分析 | `--dop-green` |
| 22 | 订单分析 | `/analysis/order` | 数据分析 | `--dop-orange` |
| 23 | 评价分析 | `/analysis/review` | 数据分析 | `--dop-pink` |
| 24 | 客服交流 | `/analysis/chat` | 数据分析 | `--dop-purple` |

> 图标 SVG 从原型文件 `console/index.html` 中复制，或用 Phosphor 图标库。每个导航项的 `<svg>` 用 `style="color:var(--dop-xxx)"` 设色。

### 4.3 `<script>` 改动

**保留**：`data()` 中的 `P`、`select()`、`handleLogout()`、`created()` 中的登录状态初始化。

**新增**：`computed.currentPageTitle`（根据 P 值返回页面标题）：

```js
computed:{
  currentPageTitle(){
    const map = {
      1:'数据全景', 2:'用户管理', 3:'部门管理', 4:'商品类别',
      5:'品牌管理', 6:'属性分组', 8:'规格参数', 9:'销售属性',
      10:'商品管理', 11:'发布商品', 16:'SKU管理', 12:'订单管理',
      17:'活动管理', 18:'会员管理', 19:'库存管理', 20:'广告设置',
      21:'用户行为分析', 22:'订单数据分析', 23:'评价数据分析', 24:'客服交流分析'
    }
    return map[this.P] || '电商后台'
  }
}
```

**删除**：`setColor()`、`computedColor()`、`index`、`ON`、`OFF`（这些是旧 table 导航用的）。

### 4.4 `<style>` 重写

**删除**：所有旧的 `#NAV` 相关样式。

**新增**：以下布局样式（从原型提取，适配 Vue）：

```css
/* 全局 */
body{ margin:0; background:var(--canvas); color:var(--ink);
  font-family:"Nunito","PingFang SC","Microsoft YaHei",system-ui,sans-serif;
  font-size:14px; line-height:1.6; }
*{box-sizing:border-box}

/* 布局 */
.app-layout{ display:grid; grid-template-columns:var(--side) 1fr; min-height:100vh; }

/* 侧栏 */
.side{ background:var(--panel); border-right:1px solid var(--line);
  display:flex; flex-direction:column; gap:4px; padding:20px 14px;
  position:sticky; top:0; height:100vh; overflow-y:auto; }
.brand{ display:flex; align-items:center; gap:10px; padding:4px 10px 22px; }
.brand-logo{ width:36px; height:36px; border-radius:10px;
  background:linear-gradient(135deg,var(--dop-purple),var(--dop-pink));
  display:grid; place-items:center; color:#fff; font-weight:800; font-size:16px; flex:none; }
.brand-text{ font-weight:800; font-size:16px; color:var(--ink); }
.nav-section{ font-size:11px; color:var(--muted); padding:14px 10px 6px;
  font-weight:700; letter-spacing:.06em; text-transform:uppercase; }
.nav-item{ display:flex; align-items:center; gap:12px; padding:10px 12px;
  border-radius:var(--r); color:var(--ink-2); font-weight:600;
  text-decoration:none; transition:all .18s ease; position:relative; }
.nav-item:hover{ background:var(--panel-2); color:var(--ink); }
.nav-item .ic{ width:20px; height:20px; flex:none; transition:transform .18s; }
.nav-item:hover .ic{ transform:scale(1.1); }
/* 当前页：深蓝色（用户明确要求） */
.nav-item.on{ background:var(--accent); color:var(--on-accent); font-weight:700;
  box-shadow:0 4px 14px rgba(30,58,138,.28); }
.nav-item.on .ic{ color:var(--on-accent)!important; }
.nav-item.on::before{ content:""; position:absolute; left:-14px; top:50%;
  transform:translateY(-50%); width:4px; height:24px; background:var(--dop-yellow);
  border-radius:0 4px 4px 0; }

/* 侧栏底部用户 */
.side-footer{ margin-top:auto; padding:12px 10px; border-top:1px solid var(--line); }
.user-chip{ display:flex; align-items:center; gap:10px; cursor:pointer; padding:6px;
  border-radius:var(--r); transition:background .15s; }
.user-chip:hover{ background:var(--panel-2); }
.avatar{ width:34px; height:34px; border-radius:50%;
  background:linear-gradient(135deg,var(--dop-cyan),var(--dop-purple));
  display:grid; place-items:center; color:#fff; font-weight:700; font-size:13px; flex:none; }
.user-name{ font-weight:700; font-size:13px; }
.user-email{ font-size:11.5px; color:var(--muted); }

/* 主区 */
.main{ display:flex; flex-direction:column; min-width:0; }
.top{ display:flex; align-items:center; gap:14px; padding:16px 28px;
  border-bottom:1px solid var(--line); background:var(--panel); }
.top h1{ font-size:20px; font-weight:800; margin:0; }
.top .sp{ margin-left:auto; }
.search-box{ display:flex; align-items:center; gap:8px; background:var(--panel-2);
  border:1px solid var(--line); border-radius:var(--r); padding:7px 14px; min-width:280px; }
.search-box input{ border:0; background:none; outline:none; font:inherit;
  color:var(--ink); width:100%; }
.search-box input::placeholder{ color:var(--muted); }
.body{ padding:24px 28px; min-width:0; }

/* 响应式：窄屏侧栏收缩为图标栏 */
@media (max-width:1180px){
  .app-layout{ grid-template-columns:72px 1fr; }
  .side{ padding:16px 8px; }
  .brand-text, .nav-item span, .nav-section, .user-name, .user-email{ display:none; }
}
```

---

## 五、执行步骤（按顺序）

1. **备份**：复制 `src/App.vue` 为 `src/App.vue.bak`（可选，用于回滚）
2. **重写 template**：按 4.1 替换 `<template>` 内容，导航项按 4.2 清单逐一添加
3. **修改 script**：按 4.3 删除旧方法，新增 `currentPageTitle` computed
4. **重写 style**：按 4.4 替换 `<style>` 内容
5. **验证**：`npm run dev` 启动，浏览器打开 `http://localhost:8081`
6. **检查点**：
   - [ ] 登录后左侧导航栏显示，24个导航项齐全
   - [ ] 当前页导航项为深蓝色背景 + 白字 + 左侧黄色指示条
   - [ ] 每个导航项图标颜色不同（多巴胺配色）
   - [ ] 点击导航项切换页面，当前态正确切换
   - [ ] 顶部栏显示当前页面标题 + 搜索框
   - [ ] 登录页不显示导航栏（全屏）
   - [ ] 窄屏（<1180px）侧栏收缩为图标栏
   - [ ] 所有子页面正常渲染，无布局错乱

---

## 六、注意事项

1. **SVG 图标**：从原型文件复制 SVG，不要用 emoji。每个 `<svg>` 必须有 `viewBox="0 0 256 256" fill="currentColor"`。
2. **router-link 的 @click**：用 `@click.native="select(N)"`，因为 router-link 是组件，需要 `.native` 修饰符。
3. **登录页判断**：`v-if="isLoggedIn && $route.path !== '/login'"` 确保登录页不显示导航。
4. **P 值持久化**：`select()` 中保留 `localStorage.setItem("P", _index)`，刷新后保持当前态。
5. **不修改子组件**：所有子页面（User.vue、Brand.vue 等）不需要改动，它们在 `.body` 的 `<router-view/>` 中渲染。
6. **旧样式清理**：确保删除所有 `#NAV`、`td a:visited`、`td a:link` 等旧样式，避免冲突。

---

## 七、验收标准

| 维度 | 标准 |
|------|------|
| 布局 | 左侧固定导航 240px + 顶部栏 + 内容区，无横向滚动 |
| 导航 | 24个导航项按4组排列（主导航/商品/运营/数据分析），图标+文字 |
| 当前态 | 深蓝 `#1E3A8A` 背景 + 白字 + 左侧黄色 `#FFD93D` 指示条 |
| 配色 | 每个导航项图标不同多巴胺色，页面底 `#F5F3EF` 暖调 |
| 交互 | 点击导航切换页面和当前态，刷新后保持当前态 |
| 登录页 | 全屏显示，无导航栏 |
| 响应式 | <1180px 侧栏收缩为 72px 图标栏 |
| 子页面 | 所有已有页面正常渲染，功能不受影响 |

---

## 八、原型参考

可运行原型：`D:\vue-space\mypro5\ui-design-runs\你是指挥席先用douba-20260917-234816-a10d\console\index.html`
浏览器直接打开查看完整交互效果（含6种状态切换）。
