import { createRouter, createWebHistory } from 'vue-router'
import { checkLoginState, clearLoginState } from '@/utils/auth'
import { useUserStore } from '@/stores/user'

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
    path: '/diary/:id',
    name: 'Detail',
    component: () => import('@/views/Detail.vue'),
    meta: { title: '美好回忆', requiresAuth: true }
  },
  {
    path: '/diaries/date/:date',
    name: 'UserDiaryList',
    component: () => import('@/views/DiaryList.vue'),
    meta: { title: '日记列表', requiresAuth: true }
  },
  {
    path: '/letters',
    name: 'LetterBox',
    component: () => import('@/views/LetterBox.vue'),
    meta: { title: '我的信箱', requiresAuth: true }
  },
  {
    path: '/letter/:id',
    name: 'LetterDetail',
    component: () => import('@/views/LetterDetail.vue'),
    meta: { title: '信件详情', requiresAuth: true }
  },
  {
    path: '/letter/edit/:id',
    name: 'EditLetter',
    component: () => import('@/views/EditLetter.vue'),
    meta: { title: '编辑信件', requiresAuth: true }
  },
  {
    path: '/share/diary/:shareToken',
    name: 'SharedDiary',
    component: () => import('@/views/SharedDiary.vue'),
    meta: { title: '分享的日记', requiresAuth: false }
  },
  {
    path: '/share/letter/:shareToken',
    name: 'SharedLetter',
    component: () => import('@/views/SharedLetter.vue'),
    meta: { title: '分享的信件', requiresAuth: false }
  },
  {
    path: '/chat-record',
    name: 'ChatRecord',
    component: () => import('@/views/ChatRecord.vue'),
    meta: { title: '聊天记录', requiresAuth: true }
  },


  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
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
      },
      {
        path: 'write-letter',
        name: 'WriteLetter',
        component: () => import('@/views/admin/WriteLetter.vue')
      },
      {
        path: 'letters',
        name: 'LetterList',
        component: () => import('@/views/admin/LetterList.vue')
      },
      {
        path: 'chat-records',
        name: 'ChatRecordList',
        component: () => import('@/views/admin/ChatRecordList.vue')
      },
      {
        path: 'chat-record/create',
        name: 'CreateChatRecord',
        component: () => import('@/views/admin/CreateChatRecord.vue')
      },
      {
        path: 'chat-record/edit/:id',
        name: 'EditChatRecord',
        component: () => import('@/views/admin/EditChatRecord.vue')
      },
      {
        path: 'system-config',
        name: 'AdminSystemConfig',
        component: () => import('@/views/admin/SystemConfigList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '记录美好回忆'

  // 检查是否需要登录
  if (to.meta.requiresAuth === true) {
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
    
    // 如果需要登录且已登录，初始化用户状态
    try {
      const userStore = useUserStore()
      if (!userStore.isLoggedIn || !userStore.userInfo) {
        await userStore.initUserState()
      }
    } catch (error) {
      console.error('初始化用户状态失败:', error)
      // 如果初始化失败，清除登录状态并跳转到登录页
      clearLoginState()
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