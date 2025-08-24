<template>
  <div class="admin-layout page-container">

    
    <div class="content">

          <!-- 二级导航菜单 -->
      <div class="sub-nav-menu" v-if="showSubNav">
        <van-grid :column-num="3" :border="false">
                     <van-grid-item 
             icon="wap-home-o" 
             text="回到用户端" 
             @click="goToFrontend"
           />
           <van-grid-item 
             icon="star-o" 
             text="回忆配置" 
             @click="$router.push('/admin/system-config')"
           />
           <van-grid-item 
             icon="close" 
             text="退出登录" 
             @click="logout"
           />
        </van-grid>
      </div>
      <!-- 导航菜单 -->
      <div class="nav-menu">
        <van-grid :column-num="3" :border="false">
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
        </van-grid>
        
        <van-grid :column-num="3" :border="false" class="second-row">
          <van-grid-item 
            icon="edit" 
            text="写封信" 
            @click="$router.push('/admin/write-letter')"
            :class="{ active: $route.path === '/admin/write-letter' }"
          />
          <van-grid-item 
            icon="envelop-o" 
            text="我的信件" 
            @click="$router.push('/admin/letters')"
            :class="{ active: $route.path === '/admin/letters' }"
          />
          <van-grid-item 
            icon="chat-o" 
            text="聊天记录" 
            @click="$router.push('/admin/chat-records')"
            :class="{ active: $route.path === '/admin/chat-records' }"
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
import { computed } from 'vue'
import { clearLoginState } from '@/utils/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()

const showSubNav = computed(() => {
  return true // 总是显示二级导航
})

const goToFrontend = () => {
  router.push('/')
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



.content {
  padding: 20px;
}

.nav-menu {
  background: white;
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  
  .second-row {
    margin-top: 10px;
  }
  
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
    
    .van-grid-item__text {
      font-size: 12px;
      line-height: 1.2;
      margin-top: 8px;
    }
    
    .van-grid-item__icon {
      font-size: 24px;
      margin-bottom: 4px;
    }
  }
}

.sub-nav-menu {
  background: white;
  border-radius: 15px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  
  :deep(.van-grid-item) {
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
    }
    
    .van-grid-item__text {
      font-size: 12px;
      line-height: 1.2;
      margin-top: 8px;
    }
    
    .van-grid-item__icon {
      font-size: 20px;
      margin-bottom: 4px;
    }
    
    &:first-child {
      .van-grid-item__icon {
        color: #667eea;
      }
      
      .van-grid-item__text {
        color: #667eea;
      }
    }
    
    &:nth-child(2) {
      .van-grid-item__icon {
        color: #ff9500;
      }
      
      .van-grid-item__text {
        color: #ff9500;
      }
    }
    
    &:last-child {
      .van-grid-item__icon {
        color: #ff3b30;
      }
      
      .van-grid-item__text {
        color: #ff3b30;
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
    
    :deep(.van-grid-item) {
      .van-grid-item__text {
        font-size: 11px;
        line-height: 1.1;
        margin-top: 6px;
      }
      
      .van-grid-item__icon {
        font-size: 20px;
        margin-bottom: 2px;
      }
    }
  }
  
  .sub-nav-menu {
    padding: 12px;
    
    :deep(.van-grid-item) {
      .van-grid-item__text {
        font-size: 10px;
        line-height: 1.1;
        margin-top: 6px;
      }
      
      .van-grid-item__icon {
        font-size: 18px;
        margin-bottom: 2px;
      }
    }
  }
  
  .page-content {
    padding: 15px;
  }
}

@media (max-width: 480px) {
  .nav-menu {
    padding: 12px;
    
    :deep(.van-grid-item) {
      .van-grid-item__text {
        font-size: 10px;
        line-height: 1.0;
        margin-top: 4px;
      }
      
      .van-grid-item__icon {
        font-size: 18px;
        margin-bottom: 2px;
      }
    }
  }
  
  .sub-nav-menu {
    padding: 10px;
    
    :deep(.van-grid-item) {
      .van-grid-item__text {
        font-size: 9px;
        line-height: 1.0;
        margin-top: 4px;
      }
      
      .van-grid-item__icon {
        font-size: 16px;
        margin-bottom: 2px;
      }
    }
  }
}
</style> 