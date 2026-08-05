import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'el-icon-s-home' }
      },
      {
        path: 'user/list',
        name: 'UserList',
        component: () => import('@/views/user/list.vue'),
        meta: { title: '用户管理', icon: 'el-icon-user' }
      },
      {
        path: 'user/category',
        name: 'CategoryList',
        component: () => import('@/views/user/category.vue'),
        meta: { title: '档案分类', icon: 'el-icon-folder' }
      },
      {
        path: 'record/list',
        name: 'RecordList',
        component: () => import('@/views/record/list.vue'),
        meta: { title: '健康记录', icon: 'el-icon-edit' }
      },
      {
        path: 'report/list',
        name: 'ReportList',
        component: () => import('@/views/report/list.vue'),
        meta: { title: '体检报告', icon: 'el-icon-reading' }
      },
      {
        path: 'report/detail/:id',
        name: 'ReportDetail',
        component: () => import('@/views/report/detail.vue'),
        meta: { title: '报告详情', hidden: true }
      },
      {
        path: 'warning/list',
        name: 'WarningList',
        component: () => import('@/views/warning/list.vue'),
        meta: { title: '健康预警', icon: 'el-icon-warning' }
      },
      {
        path: 'news/list',
        name: 'NewsList',
        component: () => import('@/views/news/list.vue'),
        meta: { title: '健康资讯', icon: 'el-icon-news' }
      },
      {
        path: 'health/data',
        name: 'HealthData',
        component: () => import('@/views/health/data.vue'),
        meta: { title: '健康数据上报', icon: 'el-icon-upload2' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') throw err
  })
}

export default router