import { createRouter, createWebHistory } from 'vue-router'
import { checkLoginState, clearLoginState } from '@/utils/auth'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Welcome',
    component: () => import('@/views/common/Welcome.vue'),
    meta: { title: '美好回忆' }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/common/Home.vue'),
    meta: { title: '我们的每一天', requiresAuth: true }
  },
  {
    path: '/calendar',
    name: 'Calendar',
    component: () => import('@/views/calendar/Calendar.vue'),
    meta: { title: '时光日历', requiresAuth: true }
  },
  {
    path: '/diary/:id',
    name: 'Detail',
    component: () => import('@/views/diary/Detail.vue'),
    meta: { title: '美好回忆', requiresAuth: true }
  },
  {
    path: '/diaries/date/:date',
    name: 'UserDiaryList',
    component: () => import('@/views/diary/DiaryList.vue'),
    meta: { title: '日记列表', requiresAuth: true }
  },
  {
    path: '/letters',
    name: 'LetterBox',
    component: () => import('@/views/letter/LetterBox.vue'),
    meta: { title: '我的信箱', requiresAuth: true }
  },
  {
    path: '/letter/:id',
    name: 'LetterDetail',
    component: () => import('@/views/letter/LetterDetail.vue'),
    meta: { title: '信件详情', requiresAuth: true }
  },

  {
    path: '/share/diary/:shareToken',
    name: 'SharedDiary',
    component: () => import('@/views/diary/SharedDiary.vue'),
    meta: { title: '分享的日记', requiresAuth: false }
  },
  {
    path: '/share/letter/:shareToken',
    name: 'SharedLetter',
    component: () => import('@/views/letter/SharedLetter.vue'),
    meta: { title: '分享的信件', requiresAuth: false }
  },
  {
    path: '/chat-record',
    name: 'ChatRecord',
    component: () => import('@/views/chat/ChatRecord.vue'),
    meta: { title: '聊天记录', requiresAuth: true }
  },
  {
    path: '/anniversary-list',
    name: 'AnniversaryList',
    component: () => import('@/views/calendar/AnniversaryList.vue'),
    meta: { title: '我们的纪念日', requiresAuth: true }
  },

  // 电影相关路由
  {
    path: '/movies',
    name: 'MovieList',
    component: () => import('@/views/movie/MovieList.vue'),
    meta: { title: '一起看电影', requiresAuth: true }
  },
  {
    path: '/create-movie',
    name: 'CreateMovie',
    component: () => import('@/views/movie/CreateMovie.vue'),
    meta: { title: '上传电影', requiresAuth: true }
  },
  {
    path: '/edit-movie/:id',
    name: 'EditMovie',
    component: () => import('@/views/movie/EditMovie.vue'),
    meta: { title: '编辑电影', requiresAuth: true }
  },
  {
    path: '/movie/:id',
    name: 'MovieDetail',
    component: () => import('@/views/movie/MovieDetail.vue'),
    meta: { title: '电影详情', requiresAuth: true }
  },
  {
    path: '/join-room',
    name: 'JoinRoom',
    component: () => import('@/views/movie/JoinRoom.vue'),
    meta: { title: '加入房间', requiresAuth: true }
  },
  {
    path: '/movie-room/:roomCode',
    name: 'MovieRoom',
    component: () => import('@/views/movie/MovieRoom.vue'),
    meta: { title: '电影房间', requiresAuth: true }
  },

  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/email-register',
    name: 'EmailRegister',
    component: () => import('@/views/auth/EmailRegister.vue'),
    meta: { title: '邮箱注册' }
  },
  {
    path: '/edit-profile',
    name: 'EditProfile',
    component: () => import('@/views/profile/EditProfile.vue'),
    meta: { title: '编辑个人信息', requiresAuth: true }
  },
  {
    path: '/love-tree',
    name: 'LoveTree',
    component: () => import('@/views/special/LoveTree.vue'),
    meta: { title: '樱花树', requiresAuth: false }
  },
  {
    path: '/love-tree-demo',
    name: 'LoveTreeDemo',
    component: () => import('@/views/special/LoveTreeDemo.vue'),
    meta: { title: '樱花树演示', requiresAuth: false }
  },
  {
    path: '/love',
    name: 'Love',
    component: () => import('@/views/special/Love.vue'),
    meta: { title: '爱的告白', requiresAuth: false }
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
        path: 'letter/edit/:id',
        name: 'EditLetter',
        component: () => import('@/views/letter/EditLetter.vue')
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