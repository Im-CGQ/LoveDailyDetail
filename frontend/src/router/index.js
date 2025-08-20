import { createRouter, createWebHistory } from 'vue-router'
import { checkLoginState, clearLoginState } from '@/utils/auth'

const routes = [
  {
    path: '/',
    name: 'Welcome',
    component: () => import('@/views/Welcome.vue'),
    meta: { title: '美好回忆' }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '我们的每一天', requiresAuth: true }
  },
  {
    path: '/calendar',
    name: 'Calendar',
    component: () => import('@/views/Calendar.vue'),
    meta: { title: '时光日历', requiresAuth: true }
  },
  {
    path: '/detail/:date',
    name: 'Detail',
    component: () => import('@/views/Detail.vue'),
    meta: { title: '美好回忆', requiresAuth: true }
  },
  {
    path: '/test-scroll',
    name: 'TestScroll',
    component: () => import('@/views/TestScroll.vue'),
    meta: { title: '滚动测试', requiresAuth: true }
  },
  {
    path: '/test-login',
    name: 'TestLogin',
    component: () => import('@/views/TestLogin.vue'),
    meta: { title: '登录测试' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { title: '后台管理', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'AdminHome',
        component: () => import('@/views/admin/AdminHome.vue')
      },
      {
        path: 'diary/create',
        name: 'CreateDiary',
        component: () => import('@/views/admin/CreateDiary.vue')
      },
      {
        path: 'diary/edit/:id',
        name: 'EditDiary',
        component: () => import('@/views/admin/EditDiary.vue')
      },
      {
        path: 'diary/list',
        name: 'DiaryList',
        component: () => import('@/views/admin/DiaryList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '记录和女朋友的每一天'
  
  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    if (!checkLoginState()) {
      // 跳转到登录页面，并传递目标路径
      const isAdminPage = to.path.startsWith('/admin')
      next({
        path: '/login',
        query: { 
          mode: isAdminPage ? 'admin' : 'user',
          redirect: to.fullPath 
        }
      })
      return
    }
  }
  
  next()
})

export default router 