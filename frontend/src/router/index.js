import Vue from 'vue'
import Router from 'vue-router'
import User from '@/components/User'
import Dept from '@/components/Dept'
import Brand from '@/components/Brand'
import Category from '@/components/Category'
import PublishBaseInfo from '@/components/PublishBaseInfo'
import SetGoodsAttr from '@/components/SetGoodsAttr'
import SetSaleAttr from '@/components/SetSaleAttr'
import SetSku from '@/components/SetSku'
import PublishComplete from '@/components/PublishComplete'
import GoodsAttrManage from '@/components/GoodsAttrManage'
import SaleAttrManage from '@/components/SaleAttrManage'
import OrderManage from '@/components/OrderManage'
import CouponManage from '@/components/CouponManage'
import MemberManage from '@/components/MemberManage'
import InventoryManage from '@/components/InventoryManage'
import AdvertManage from '@/components/AdvertManage'
import Login from '@/components/Login'
import AttrGroupManage from '@/components/AttrGroupManage'
import RoleManage from '@/components/RoleManage'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/user',
      name: 'user',
      component: User
    },
    {
      path: '/dept',
      name: 'dept',
      component: Dept
    },
    {
      path: '/roleManage',
      name: 'roleManage',
      component: RoleManage
    },
    {
      path: '/brand',
      name: 'Brand',
      component: Brand
    },
    {
      path: '/category',
      name: 'Category',
      component: Category
    },
    {
      path: '/attrGroup',
      name: 'attrGroup',
      component: AttrGroupManage
    },
    {
      path: '/publishBaseInfo',
      name: 'publishBaseInfo',
      component: PublishBaseInfo
    },
    {
      path: '/setGoodsAttr/:categoryId',
      name: 'setGoodsAttr',
      component: SetGoodsAttr
    },
    {
      path: '/setSaleAttr/:categoryId',
      name: 'setSaleAttr',
      component: SetSaleAttr
    },
    {
      path: '/setSku/:pubKey',
      name: 'setSku',
      component: SetSku
    },
    {
      path: '/publishComplete/:pubKey',
      name: 'publishComplete',
      component: PublishComplete
    },
    {
      path: '/goodsManage',
      name: 'goodsManage',
      component: () => import('@/components/GoodsManage.vue')
    },
    {
      path: '/skuManage',
      name: 'skuManage',
      component: () => import('@/components/SkuManage.vue')
    },
    {
      path: '/goodsAttr',
      name: 'goodsAttr',
      component: GoodsAttrManage
    },
    {
      path: '/saleAttr',
      name: 'saleAttr',
      component: SaleAttrManage
    },
    {
      path: '/orderManage',
      name: 'orderManage',
      component: OrderManage
    },
    {
      path: '/couponManage',
      name: 'couponManage',
      component: CouponManage
    },
    {
      path: '/memberManage',
      name: 'memberManage',
      component: MemberManage
    },
    {
      path: '/inventoryManage',
      name: 'inventoryManage',
      component: InventoryManage
    },
    {
      path: '/advertManage',
      name: 'advertManage',
      component: AdvertManage
    },
    {
      path: '/dashBoard',
      name: 'dashBoard',
      component: () => import('@/components/Dashboard.vue')
    },
    {
      path: '/analysis/behavior',
      name: 'analysisBehavior',
      component: () => import('@/components/analysis/BehaviorAnalysis.vue')
    },
    {
      path: '/analysis/order',
      name: 'analysisOrder',
      component: () => import('@/components/analysis/OrderAnalysis.vue')
    },
    {
      path: '/analysis/review',
      name: 'analysisReview',
      component: () => import('@/components/analysis/ReviewAnalysis.vue')
    },
    {
      path: '/analysis/chat',
      name: 'analysisChat',
      component: () => import('@/components/analysis/ChatAnalysis.vue')
    },
    {
      path: '/simulate/user',
      name: 'simulateUser',
      component: () => import('@/components/SimulateUser.vue')
    },
  ]
})

// 全局路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  let token = window.sessionStorage.getItem('token')
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router