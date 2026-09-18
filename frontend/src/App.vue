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
          <svg class="ic" style="color:var(--dop-blue)" viewBox="0 0 256 256" fill="currentColor"><path d="M219.31,108.68l-80-80a16,16,0,0,0-22.62,0l-80,80A15.87,15.87,0,0,0,32,120v96a8,8,0,0,0,8,8h64a8,8,0,0,0,8-8V160h32v56a8,8,0,0,0,8,8h64a8,8,0,0,0,8-8V120A15.87,15.87,0,0,0,219.31,108.68ZM208,208H160V152a8,8,0,0,0-8-8H104a8,8,0,0,0-8,8v56H48V120l80-80,80,80Z"/></svg>
          <span>数据全景</span>
        </router-link>
        <router-link to="/user" class="nav-item" :class="{on: P==2}" @click.native="select(2)">
          <svg class="ic" style="color:var(--dop-cyan)" viewBox="0 0 256 256" fill="currentColor"><path d="M117.25,157.92a60,60,0,1,0-66.5,0A95.83,95.83,0,0,0,3.53,195.63a8,8,0,1,0,13.4,8.74,80,80,0,0,1,134.14,0,8,8,0,0,0,13.4-8.74A95.83,95.83,0,0,0,117.25,157.92ZM40,108a44,44,0,1,1,44,44A44.05,44.05,0,0,1,40,108Zm210.14,98.7a8,8,0,0,1-11.07-2.33A79.83,79.83,0,0,0,172,168a8,8,0,0,1,0-16,44,44,0,1,0-16.34-84.87,8,8,0,1,1-5.94-14.85,60,60,0,0,1,55.53,105.64,95.83,95.83,0,0,1,47.22,37.71A8,8,0,0,1,250.14,206.7Z"/></svg>
          <span>用户管理</span>
        </router-link>
        <router-link to="/dept" class="nav-item" :class="{on: P==3}" @click.native="select(3)">
          <svg class="ic" style="color:var(--dop-green)" viewBox="0 0 256 256" fill="currentColor"><path d="M72,104a8,8,0,0,1,8-8h96a8,8,0,0,1,0,16H80A8,8,0,0,1,72,104Zm8,40h96a8,8,0,0,0,0-16H80a8,8,0,0,0,0,16ZM232,56V208a8,8,0,0,1-11.58,7.15L192,200.94l-28.42,14.21a8,8,0,0,1-7.16,0L128,200.94,99.58,215.15a8,8,0,0,1-7.16,0L64,200.94,35.58,215.15A8,8,0,0,1,24,208V56A16,16,0,0,1,40,40H216A16,16,0,0,1,232,56Zm-16,0H40V195.06l20.42-10.22a8,8,0,0,1,7.16,0L96,199.06l28.42-14.22a8,8,0,0,1,7.16,0L160,199.06l28.42-14.22a8,8,0,0,1,7.16,0L216,195.06Z"/></svg>
          <span>部门管理</span>
        </router-link>

        <div class="nav-section">商品</div>
        <router-link to="/category" class="nav-item" :class="{on: P==4}" @click.native="select(4)">
          <svg class="ic" style="color:var(--dop-orange)" viewBox="0 0 256 256" fill="currentColor"><path d="M223.68,66.15,135.68,18a15.88,15.88,0,0,0-15.36,0l-88,48.17a16,16,0,0,0-8.32,14v95.64a16,16,0,0,0,8.32,14l88,48.17a15.88,15.88,0,0,0,15.36,0l88-48.17a16,16,0,0,0,8.32-14V80.18A16,16,0,0,0,223.68,66.15ZM128,32l80.34,44-29.77,16.3-80.35-44ZM128,120,47.66,76l33.9-18.56,80.34,44ZM40,90l80,43.78v85.79L40,175.82Zm176,85.78h0l-80,43.79V133.82l32-17.51V152a8,8,0,0,0,16,0V107.55L216,90v85.77Z"/></svg>
          <span>商品类别</span>
        </router-link>
        <router-link to="/brand" class="nav-item" :class="{on: P==5}" @click.native="select(5)">
          <svg class="ic" style="color:var(--dop-pink)" viewBox="0 0 256 256" fill="currentColor"><path d="M128,80a48,48,0,1,0,48,48A48.05,48.05,0,0,0,128,80Zm0,80a32,32,0,1,1,32-32A32,32,0,0,1,128,160Zm88-29.84q.06-2.16,0-4.32l14.92-18.64a8,8,0,0,0,1.48-7.06,107.21,107.21,0,0,0-10.88-26.25,8,8,0,0,0-6-3.93l-23.72-2.64q-1.48-1.56-3-3L186,40.54a8,8,0,0,0-3.94-6,107.71,107.71,0,0,0-26.25-10.87,8,8,0,0,0-7.06,1.49L130.16,40Q128,40,125.84,40L107.2,25.11a8,8,0,0,0-7.06-1.48A107.6,107.6,0,0,0,73.89,34.51a8,8,0,0,0-3.93,6L67.32,64.27q-1.56,1.49-3,3L40.54,70a8,8,0,0,0-6,3.94,107.71,107.71,0,0,0-10.87,26.25,8,8,0,0,0,1.49,7.06L40,125.84Q40,128,40,130.16L25.11,148.8a8,8,0,0,0-1.48,7.06,107.21,107.21,0,0,0,10.88,26.25,8,8,0,0,0,6,3.93l23.72,2.64q1.49,1.56,3,3L70,215.46a8,8,0,0,0,3.94,6,107.71,107.71,0,0,0,26.25,10.87,8,8,0,0,0,7.06-1.49L125.84,216q2.16.06,4.32,0l18.64,14.92a8,8,0,0,0,7.06,1.48,107.21,107.21,0,0,0,26.25-10.88,8,8,0,0,0,3.93-6l2.64-23.72q1.56-1.48,3-3L215.46,186a8,8,0,0,0,6-3.94,107.71,107.71,0,0,0,10.87-26.25,8,8,0,0,0-1.49-7.06Zm-16.1-6.5a73.93,73.93,0,0,1,0,8.68,8,8,0,0,0,1.74,5.48l14.19,17.73a91.57,91.57,0,0,1-6.23,15L187,173.11a8,8,0,0,0-5.1,2.64,74.11,74.11,0,0,1-6.14,6.14,8,8,0,0,0-2.64,5.1l-2.51,22.58a91.32,91.32,0,0,1-15,6.23l-17.74-14.19a8,8,0,0,0-5-1.75h-.48a73.93,73.93,0,0,1-8.68,0,8,8,0,0,0-5.48,1.74L100.45,215.8a91.57,91.57,0,0,1-15-6.23L82.89,187a8,8,0,0,0-2.64-5.1,74.11,74.11,0,0,1-6.14-6.14,8,8,0,0,0-5.1-2.64L46.43,170.6a91.32,91.32,0,0,1-6.23-15l14.19-17.74a8,8,0,0,0,1.74-5.48,73.93,73.93,0,0,1,0-8.68,8,8,0,0,0-1.74-5.48L40.2,100.45a91.57,91.57,0,0,1,6.23-15L69,82.89a8,8,0,0,0,5.1-2.64,74.11,74.11,0,0,1,6.14-6.14A8,8,0,0,0,82.89,69L85.4,46.43a91.32,91.32,0,0,1,15-6.23l17.74,14.19a8,8,0,0,0,5.48,1.74,73.93,73.93,0,0,1,8.68,0,8,8,0,0,0,5.48-1.74L155.55,40.2a91.57,91.57,0,0,1,15,6.23L173.11,69a8,8,0,0,0,2.64,5.1,74.11,74.11,0,0,1,6.14,6.14,8,8,0,0,0,5.1,2.64l22.58,2.51a91.32,91.32,0,0,1,6.23,15l-14.19,17.74A8,8,0,0,0,199.87,123.66Z"/></svg>
          <span>品牌管理</span>
        </router-link>
        <router-link to="/attrGroup" class="nav-item" :class="{on: P==6}" @click.native="select(6)">
          <svg class="ic" style="color:var(--dop-purple)" viewBox="0 0 256 256" fill="currentColor"><path d="M224,200h-8V40a8,8,0,0,0-8-8H152a8,8,0,0,0-8,8V80H96a8,8,0,0,0-8,8v40H48a8,8,0,0,0-8,8v64H32a8,8,0,0,0,0,16H224a8,8,0,0,0,0-16ZM160,48h40V200H160ZM104,96h40V200H104ZM56,144H88v56H56Z"/></svg>
          <span>属性分组</span>
        </router-link>
        <router-link to="/goodsAttr" class="nav-item" :class="{on: P==8}" @click.native="select(8)">
          <svg class="ic" style="color:var(--dop-yellow)" viewBox="0 0 256 256" fill="currentColor"><path d="M223.68,66.15,135.68,18a15.88,15.88,0,0,0-15.36,0l-88,48.17a16,16,0,0,0-8.32,14v95.64a16,16,0,0,0,8.32,14l88,48.17a15.88,15.88,0,0,0,15.36,0l88-48.17a16,16,0,0,0,8.32-14V80.18A16,16,0,0,0,223.68,66.15ZM128,32l80.34,44-29.77,16.3-80.35-44ZM128,120,47.66,76l33.9-18.56,80.34,44ZM40,90l80,43.78v85.79L40,175.82Zm176,85.78h0l-80,43.79V133.82l32-17.51V152a8,8,0,0,0,16,0V107.55L216,90v85.77Z"/></svg>
          <span>规格参数</span>
        </router-link>
        <router-link to="/saleAttr" class="nav-item" :class="{on: P==9}" @click.native="select(9)">
          <svg class="ic" style="color:var(--dop-blue)" viewBox="0 0 256 256" fill="currentColor"><path d="M224,200h-8V40a8,8,0,0,0-8-8H152a8,8,0,0,0-8,8V80H96a8,8,0,0,0-8,8v40H48a8,8,0,0,0-8,8v64H32a8,8,0,0,0,0,16H224a8,8,0,0,0,0-16ZM160,48h40V200H160ZM104,96h40V200H104ZM56,144H88v56H56Z"/></svg>
          <span>销售属性</span>
        </router-link>
        <router-link to="/goodsManage" class="nav-item" :class="{on: P==10}" @click.native="select(10)">
          <svg class="ic" style="color:var(--dop-cyan)" viewBox="0 0 256 256" fill="currentColor"><path d="M223.68,66.15,135.68,18a15.88,15.88,0,0,0-15.36,0l-88,48.17a16,16,0,0,0-8.32,14v95.64a16,16,0,0,0,8.32,14l88,48.17a15.88,15.88,0,0,0,15.36,0l88-48.17a16,16,0,0,0,8.32-14V80.18A16,16,0,0,0,223.68,66.15ZM128,32l80.34,44-29.77,16.3-80.35-44ZM128,120,47.66,76l33.9-18.56,80.34,44ZM40,90l80,43.78v85.79L40,175.82Zm176,85.78h0l-80,43.79V133.82l32-17.51V152a8,8,0,0,0,16,0V107.55L216,90v85.77Z"/></svg>
          <span>商品管理</span>
        </router-link>
        <router-link to="/publishBaseInfo" class="nav-item" :class="{on: P==11}" @click.native="select(11)">
          <svg class="ic" style="color:var(--dop-green)" viewBox="0 0 256 256" fill="currentColor"><path d="M72,104a8,8,0,0,1,8-8h96a8,8,0,0,1,0,16H80A8,8,0,0,1,72,104Zm8,40h96a8,8,0,0,0,0-16H80a8,8,0,0,0,0,16ZM232,56V208a8,8,0,0,1-11.58,7.15L192,200.94l-28.42,14.21a8,8,0,0,1-7.16,0L128,200.94,99.58,215.15a8,8,0,0,1-7.16,0L64,200.94,35.58,215.15A8,8,0,0,1,24,208V56A16,16,0,0,1,40,40H216A16,16,0,0,1,232,56Zm-16,0H40V195.06l20.42-10.22a8,8,0,0,1,7.16,0L96,199.06l28.42-14.22a8,8,0,0,1,7.16,0L160,199.06l28.42-14.22a8,8,0,0,1,7.16,0L216,195.06Z"/></svg>
          <span>发布商品</span>
        </router-link>
        <router-link to="/skuManage" class="nav-item" :class="{on: P==16}" @click.native="select(16)">
          <svg class="ic" style="color:var(--dop-orange)" viewBox="0 0 256 256" fill="currentColor"><path d="M223.68,66.15,135.68,18a15.88,15.88,0,0,0-15.36,0l-88,48.17a16,16,0,0,0-8.32,14v95.64a16,16,0,0,0,8.32,14l88,48.17a15.88,15.88,0,0,0,15.36,0l88-48.17a16,16,0,0,0,8.32-14V80.18A16,16,0,0,0,223.68,66.15ZM128,32l80.34,44-29.77,16.3-80.35-44ZM128,120,47.66,76l33.9-18.56,80.34,44ZM40,90l80,43.78v85.79L40,175.82Zm176,85.78h0l-80,43.79V133.82l32-17.51V152a8,8,0,0,0,16,0V107.55L216,90v85.77Z"/></svg>
          <span>SKU管理</span>
        </router-link>

        <div class="nav-section">运营</div>
        <router-link to="/orderManage" class="nav-item" :class="{on: P==12}" @click.native="select(12)">
          <svg class="ic" style="color:var(--dop-pink)" viewBox="0 0 256 256" fill="currentColor"><path d="M72,104a8,8,0,0,1,8-8h96a8,8,0,0,1,0,16H80A8,8,0,0,1,72,104Zm8,40h96a8,8,0,0,0,0-16H80a8,8,0,0,0,0,16ZM232,56V208a8,8,0,0,1-11.58,7.15L192,200.94l-28.42,14.21a8,8,0,0,1-7.16,0L128,200.94,99.58,215.15a8,8,0,0,1-7.16,0L64,200.94,35.58,215.15A8,8,0,0,1,24,208V56A16,16,0,0,1,40,40H216A16,16,0,0,1,232,56Zm-16,0H40V195.06l20.42-10.22a8,8,0,0,1,7.16,0L96,199.06l28.42-14.22a8,8,0,0,1,7.16,0L160,199.06l28.42-14.22a8,8,0,0,1,7.16,0L216,195.06Z"/></svg>
          <span>订单管理</span>
        </router-link>
        <router-link to="/couponManage" class="nav-item" :class="{on: P==17}" @click.native="select(17)">
          <svg class="ic" style="color:var(--dop-purple)" viewBox="0 0 256 256" fill="currentColor"><path d="M128,80a48,48,0,1,0,48,48A48.05,48.05,0,0,0,128,80Zm0,80a32,32,0,1,1,32-32A32,32,0,0,1,128,160Zm88-29.84q.06-2.16,0-4.32l14.92-18.64a8,8,0,0,0,1.48-7.06,107.21,107.21,0,0,0-10.88-26.25,8,8,0,0,0-6-3.93l-23.72-2.64q-1.48-1.56-3-3L186,40.54a8,8,0,0,0-3.94-6,107.71,107.71,0,0,0-26.25-10.87,8,8,0,0,0-7.06,1.49L130.16,40Q128,40,125.84,40L107.2,25.11a8,8,0,0,0-7.06-1.48A107.6,107.6,0,0,0,73.89,34.51a8,8,0,0,0-3.93,6L67.32,64.27q-1.56,1.49-3,3L40.54,70a8,8,0,0,0-6,3.94,107.71,107.71,0,0,0-10.87,26.25,8,8,0,0,0,1.49,7.06L40,125.84Q40,128,40,130.16L25.11,148.8a8,8,0,0,0-1.48,7.06,107.21,107.21,0,0,0,10.88,26.25,8,8,0,0,0,6,3.93l23.72,2.64q1.49,1.56,3,3L70,215.46a8,8,0,0,0,3.94,6,107.71,107.71,0,0,0,26.25,10.87,8,8,0,0,0,7.06-1.49L125.84,216q2.16.06,4.32,0l18.64,14.92a8,8,0,0,0,7.06,1.48,107.21,107.21,0,0,0,26.25-10.88,8,8,0,0,0,3.93-6l2.64-23.72q1.56-1.48,3-3L215.46,186a8,8,0,0,0,6-3.94,107.71,107.71,0,0,0,10.87-26.25,8,8,0,0,0-1.49-7.06Zm-16.1-6.5a73.93,73.93,0,0,1,0,8.68,8,8,0,0,0,1.74,5.48l14.19,17.73a91.57,91.57,0,0,1-6.23,15L187,173.11a8,8,0,0,0-5.1,2.64,74.11,74.11,0,0,1-6.14,6.14,8,8,0,0,0-2.64,5.1l-2.51,22.58a91.32,91.32,0,0,1-15,6.23l-17.74-14.19a8,8,0,0,0-5-1.75h-.48a73.93,73.93,0,0,1-8.68,0,8,8,0,0,0-5.48,1.74L100.45,215.8a91.57,91.57,0,0,1-15-6.23L82.89,187a8,8,0,0,0-2.64-5.1,74.11,74.11,0,0,1-6.14-6.14,8,8,0,0,0-5.1-2.64L46.43,170.6a91.32,91.32,0,0,1-6.23-15l14.19-17.74a8,8,0,0,0,1.74-5.48,73.93,73.93,0,0,1,0-8.68,8,8,0,0,0-1.74-5.48L40.2,100.45a91.57,91.57,0,0,1,6.23-15L69,82.89a8,8,0,0,0,5.1-2.64,74.11,74.11,0,0,1,6.14-6.14A8,8,0,0,0,82.89,69L85.4,46.43a91.32,91.32,0,0,1,15-6.23l17.74,14.19a8,8,0,0,0,5.48,1.74,73.93,73.93,0,0,1,8.68,0,8,8,0,0,0,5.48-1.74L155.55,40.2a91.57,91.57,0,0,1,15,6.23L173.11,69a8,8,0,0,0,2.64,5.1,74.11,74.11,0,0,1,6.14,6.14,8,8,0,0,0,5.1,2.64l22.58,2.51a91.32,91.32,0,0,1,6.23,15l-14.19,17.74A8,8,0,0,0,199.87,123.66Z"/></svg>
          <span>活动管理</span>
        </router-link>
        <router-link to="/memberManage" class="nav-item" :class="{on: P==18}" @click.native="select(18)">
          <svg class="ic" style="color:var(--dop-yellow)" viewBox="0 0 256 256" fill="currentColor"><path d="M117.25,157.92a60,60,0,1,0-66.5,0A95.83,95.83,0,0,0,3.53,195.63a8,8,0,1,0,13.4,8.74,80,80,0,0,1,134.14,0,8,8,0,0,0,13.4-8.74A95.83,95.83,0,0,0,117.25,157.92ZM40,108a44,44,0,1,1,44,44A44.05,44.05,0,0,1,40,108Zm210.14,98.7a8,8,0,0,1-11.07-2.33A79.83,79.83,0,0,0,172,168a8,8,0,0,1,0-16,44,44,0,1,0-16.34-84.87,8,8,0,1,1-5.94-14.85,60,60,0,0,1,55.53,105.64,95.83,95.83,0,0,1,47.22,37.71A8,8,0,0,1,250.14,206.7Z"/></svg>
          <span>会员管理</span>
        </router-link>
        <router-link to="/inventoryManage" class="nav-item" :class="{on: P==19}" @click.native="select(19)">
          <svg class="ic" style="color:var(--dop-blue)" viewBox="0 0 256 256" fill="currentColor"><path d="M224,200h-8V40a8,8,0,0,0-8-8H152a8,8,0,0,0-8,8V80H96a8,8,0,0,0-8,8v40H48a8,8,0,0,0-8,8v64H32a8,8,0,0,0,0,16H224a8,8,0,0,0,0-16ZM160,48h40V200H160ZM104,96h40V200H104ZM56,144H88v56H56Z"/></svg>
          <span>库存管理</span>
        </router-link>
        <router-link to="/advertManage" class="nav-item" :class="{on: P==20}" @click.native="select(20)">
          <svg class="ic" style="color:var(--dop-cyan)" viewBox="0 0 256 256" fill="currentColor"><path d="M72,104a8,8,0,0,1,8-8h96a8,8,0,0,1,0,16H80A8,8,0,0,1,72,104Zm8,40h96a8,8,0,0,0,0-16H80a8,8,0,0,0,0,16ZM232,56V208a8,8,0,0,1-11.58,7.15L192,200.94l-28.42,14.21a8,8,0,0,1-7.16,0L128,200.94,99.58,215.15a8,8,0,0,1-7.16,0L64,200.94,35.58,215.15A8,8,0,0,1,24,208V56A16,16,0,0,1,40,40H216A16,16,0,0,1,232,56Zm-16,0H40V195.06l20.42-10.22a8,8,0,0,1,7.16,0L96,199.06l28.42-14.22a8,8,0,0,1,7.16,0L160,199.06l28.42-14.22a8,8,0,0,1,7.16,0L216,195.06Z"/></svg>
          <span>广告设置</span>
        </router-link>

        <div class="nav-section">数据分析</div>
        <router-link to="/simulate/user" class="nav-item" :class="{on: P==25}" @click.native="select(25)">
          <svg class="ic" style="color:var(--dop-cyan)" viewBox="0 0 256 256" fill="currentColor"><path d="M128,16A112,112,0,1,0,240,128,112.13,112.13,0,0,0,128,16Zm0,200a88,88,0,1,1,88-88A88.1,88.1,0,0,1,128,216Zm-16-128h32v32h32v32h-32v32H112V152H80V120h32Z"/></svg>
          <span>模拟用户</span>
        </router-link>
        <router-link to="/analysis/behavior" class="nav-item" :class="{on: P==21}" @click.native="select(21)">
          <svg class="ic" style="color:var(--dop-green)" viewBox="0 0 256 256" fill="currentColor"><path d="M224,200h-8V40a8,8,0,0,0-8-8H152a8,8,0,0,0-8,8V80H96a8,8,0,0,0-8,8v40H48a8,8,0,0,0-8,8v64H32a8,8,0,0,0,0,16H224a8,8,0,0,0,0-16ZM160,48h40V200H160ZM104,96h40V200H104ZM56,144H88v56H56Z"/></svg>
          <span>用户行为</span>
        </router-link>
        <router-link to="/analysis/order" class="nav-item" :class="{on: P==22}" @click.native="select(22)">
          <svg class="ic" style="color:var(--dop-orange)" viewBox="0 0 256 256" fill="currentColor"><path d="M72,104a8,8,0,0,1,8-8h96a8,8,0,0,1,0,16H80A8,8,0,0,1,72,104Zm8,40h96a8,8,0,0,0,0-16H80a8,8,0,0,0,0,16ZM232,56V208a8,8,0,0,1-11.58,7.15L192,200.94l-28.42,14.21a8,8,0,0,1-7.16,0L128,200.94,99.58,215.15a8,8,0,0,1-7.16,0L64,200.94,35.58,215.15A8,8,0,0,1,24,208V56A16,16,0,0,1,40,40H216A16,16,0,0,1,232,56Zm-16,0H40V195.06l20.42-10.22a8,8,0,0,1,7.16,0L96,199.06l28.42-14.22a8,8,0,0,1,7.16,0L160,199.06l28.42-14.22a8,8,0,0,1,7.16,0L216,195.06Z"/></svg>
          <span>订单分析</span>
        </router-link>
        <router-link to="/analysis/review" class="nav-item" :class="{on: P==23}" @click.native="select(23)">
          <svg class="ic" style="color:var(--dop-pink)" viewBox="0 0 256 256" fill="currentColor"><path d="M117.25,157.92a60,60,0,1,0-66.5,0A95.83,95.83,0,0,0,3.53,195.63a8,8,0,1,0,13.4,8.74,80,80,0,0,1,134.14,0,8,8,0,0,0,13.4-8.74A95.83,95.83,0,0,0,117.25,157.92ZM40,108a44,44,0,1,1,44,44A44.05,44.05,0,0,1,40,108Zm210.14,98.7a8,8,0,0,1-11.07-2.33A79.83,79.83,0,0,0,172,168a8,8,0,0,1,0-16,44,44,0,1,0-16.34-84.87,8,8,0,1,1-5.94-14.85,60,60,0,0,1,55.53,105.64,95.83,95.83,0,0,1,47.22,37.71A8,8,0,0,1,250.14,206.7Z"/></svg>
          <span>评价分析</span>
        </router-link>
        <router-link to="/analysis/chat" class="nav-item" :class="{on: P==24}" @click.native="select(24)">
          <svg class="ic" style="color:var(--dop-purple)" viewBox="0 0 256 256" fill="currentColor"><path d="M128,80a48,48,0,1,0,48,48A48.05,48.05,0,0,0,128,80Zm0,80a32,32,0,1,1,32-32A32,32,0,0,1,128,160Zm88-29.84q.06-2.16,0-4.32l14.92-18.64a8,8,0,0,0,1.48-7.06,107.21,107.21,0,0,0-10.88-26.25,8,8,0,0,0-6-3.93l-23.72-2.64q-1.48-1.56-3-3L186,40.54a8,8,0,0,0-3.94-6,107.71,107.71,0,0,0-26.25-10.87,8,8,0,0,0-7.06,1.49L130.16,40Q128,40,125.84,40L107.2,25.11a8,8,0,0,0-7.06-1.48A107.6,107.6,0,0,0,73.89,34.51a8,8,0,0,0-3.93,6L67.32,64.27q-1.56,1.49-3,3L40.54,70a8,8,0,0,0-6,3.94,107.71,107.71,0,0,0-10.87,26.25,8,8,0,0,0,1.49,7.06L40,125.84Q40,128,40,130.16L25.11,148.8a8,8,0,0,0-1.48,7.06,107.21,107.21,0,0,0,10.88,26.25,8,8,0,0,0,6,3.93l23.72,2.64q1.49,1.56,3,3L70,215.46a8,8,0,0,0,3.94,6,107.71,107.71,0,0,0,26.25,10.87,8,8,0,0,0,7.06-1.49L125.84,216q2.16.06,4.32,0l18.64,14.92a8,8,0,0,0,7.06,1.48,107.21,107.21,0,0,0,26.25-10.88,8,8,0,0,0,3.93-6l2.64-23.72q1.56-1.48,3-3L215.46,186a8,8,0,0,0,6-3.94,107.71,107.71,0,0,0,10.87-26.25,8,8,0,0,0-1.49-7.06Zm-16.1-6.5a73.93,73.93,0,0,1,0,8.68,8,8,0,0,0,1.74,5.48l14.19,17.73a91.57,91.57,0,0,1-6.23,15L187,173.11a8,8,0,0,0-5.1,2.64,74.11,74.11,0,0,1-6.14,6.14,8,8,0,0,0-2.64,5.1l-2.51,22.58a91.32,91.32,0,0,1-15,6.23l-17.74-14.19a8,8,0,0,0-5-1.75h-.48a73.93,73.93,0,0,1-8.68,0,8,8,0,0,0-5.48,1.74L100.45,215.8a91.57,91.57,0,0,1-15-6.23L82.89,187a8,8,0,0,0-2.64-5.1,74.11,74.11,0,0,1-6.14-6.14,8,8,0,0,0-5.1-2.64L46.43,170.6a91.32,91.32,0,0,1-6.23-15l14.19-17.74a8,8,0,0,0,1.74-5.48,73.93,73.93,0,0,1,0-8.68,8,8,0,0,0-1.74-5.48L40.2,100.45a91.57,91.57,0,0,1,6.23-15L69,82.89a8,8,0,0,0,5.1-2.64,74.11,74.11,0,0,1,6.14-6.14A8,8,0,0,0,82.89,69L85.4,46.43a91.32,91.32,0,0,1,15-6.23l17.74,14.19a8,8,0,0,0,5.48,1.74,73.93,73.93,0,0,1,8.68,0,8,8,0,0,0,5.48-1.74L155.55,40.2a91.57,91.57,0,0,1,15,6.23L173.11,69a8,8,0,0,0,2.64,5.1,74.11,74.11,0,0,1,6.14,6.14,8,8,0,0,0,5.1,2.64l22.58,2.51a91.32,91.32,0,0,1,6.23,15l-14.19,17.74A8,8,0,0,0,199.87,123.66Z"/></svg>
          <span>客服交流</span>
        </router-link>

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

<script>
export default {
  name: 'App',
  data(){
	return {
		P:1,
		isLoggedIn: false
	}
  },
  methods:{
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
	currentPageTitle(){
	  const map = {
	    1:'数据全景', 2:'用户管理', 3:'部门管理', 4:'商品类别',
	    5:'品牌管理', 6:'属性分组', 8:'规格参数', 9:'销售属性',
	    10:'商品管理', 11:'发布商品', 16:'SKU管理', 12:'订单管理',
	    17:'活动管理', 18:'会员管理', 19:'库存管理', 20:'广告设置',
	    21:'用户行为分析', 22:'订单数据分析', 23:'评价数据分析', 24:'客服交流分析',
	    25:'模拟用户'
	  }
	  return map[this.P] || '电商后台'
	}
  }
}
</script>

<style>
:root{
  /* 基底 */
  --canvas:#FFF4D6;        /* 页面底：护眼暖黄 */
  --panel:#FFFCF0;         /* 卡片/侧栏底：浅奶黄 */
  --panel-2:#FBEEC2;       /* 内嵌底（搜索框/表头）：稍深暖黄 */
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
</style>
