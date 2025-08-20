<template>
  <div class="admin-layout page-container">
    <van-nav-bar 
      title="后台管理" 
      left-text="返回前台"
      left-arrow
      @click-left="goToFrontend"
      class="nav-bar"
    />
    
    <div class="content">
      <!-- 导航菜单 -->
      <div class="nav-menu">
        <van-grid :column-num="4" :border="false">
          <van-grid-item 
            icon="home-o" 
            text="首页" 
            @click="$router.push('/admin')"
            :class="{ active: $route.path === '/admin' }"
          />
          <van-grid-item 
            icon="plus" 
            text="创建日记" 
            @click="$router.push('/admin/diary/create')"
            :class="{ active: $route.path === '/admin/diary/create' }"
          />
          <van-grid-item 
            icon="records" 
            text="日记列表" 
            @click="$router.push('/admin/diary/list')"
            :class="{ active: $route.path === '/admin/diary/list' }"
          />
          <van-grid-item 
            icon="setting-o" 
            text="退出登录" 
            @click="logout"
          />
        </van-grid>
      </div>
      
      <!-- 页面内容 -->
      <div class="page-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { clearLoginState } from '@/utils/auth'

const router = useRouter()

const goToFrontend = () => {
  router.push('/home')
}

const logout = () => {
  clearLoginState()
  router.push('/login?mode=admin')
}
</script>

<style scoped>
.admin-layout {
  background: #f5f5f5;
}

.nav-bar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  
  :deep(.van-nav-bar__title) {
    color: white !important;
    font-weight: bold;
    font-size: 18px;
  }
  
  :deep(.van-nav-bar__text) {
    color: white !important;
    font-weight: 500;
  }
  
  :deep(.van-icon) {
    color: white !important;
    font-size: 20px;
  }
  
  :deep(.van-nav-bar__content) {
    padding: 12px 16px;
  }
  
  :deep(.van-nav-bar__left) {
    .van-icon {
      color: white !important;
    }
  }
}

.content {
  padding: 20px;
}

.nav-menu {
  background: white;
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  
  :deep(.van-grid-item) {
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
    }
    
    &.active {
      .van-grid-item__icon {
        color: #ff6b9d;
      }
      
      .van-grid-item__text {
        color: #ff6b9d;
        font-weight: 600;
      }
    }
  }
}

.page-content {
  background: white;
  border-radius: 15px;
  padding: 20px;
  min-height: 60vh;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .content {
    padding: 15px;
  }
  
  .nav-menu {
    padding: 15px;
  }
  
  .page-content {
    padding: 15px;
  }
}
</style> 