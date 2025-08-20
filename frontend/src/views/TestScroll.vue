<template>
  <div class="test-scroll romantic-bg page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <van-nav-bar title="滚动测试" left-arrow @click-left="$router.back()" class="nav-bar" />

    <div class="content">
      <div class="header float">
        <h1 class="title text-gradient-romantic">滚动测试页面</h1>
        <p class="subtitle pulse">测试内容是否可以正常滚动</p>
      </div>

      <!-- 测试内容 -->
      <div class="test-sections">
        <div v-for="i in 10" :key="i" class="test-section hover-lift glow">
          <h3 class="section-title">测试区块 {{ i }}</h3>
          <p class="section-content">
            这是第 {{ i }} 个测试区块，用于验证页面滚动功能是否正常工作。
            每个区块都包含一些示例内容，确保页面有足够的高度来测试滚动。
          </p>
          <div class="test-image">
            <img :src="`https://picsum.photos/400/200?random=${i}`" :alt="`测试图片 ${i}`" />
          </div>
        </div>
      </div>

      <!-- 底部提示 -->
      <div class="bottom-tip">
        <p>🎉 如果你能看到这个提示，说明滚动功能正常工作！</p>
        <van-button 
          type="primary" 
          size="large" 
          @click="$router.back()"
          class="back-btn btn-primary"
        >
          返回上一页
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.test-scroll {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
}

.nav-bar {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  
  :deep(.van-nav-bar__title) {
    color: white !important;
    font-weight: bold;
    font-size: 18px;
  }
  
  :deep(.van-icon) {
    color: white !important;
    font-size: 20px;
  }
  
  :deep(.van-nav-bar__text) {
    color: white !important;
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
  position: relative;
  z-index: 2;
  padding-bottom: 40px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
  
  .title {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 10px;
  }
  
  .subtitle {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.8);
  }
}

.test-sections {
  .test-section {
    background: rgba(255, 255, 255, 0.15);
    border-radius: 20px;
    padding: 25px;
    margin-bottom: 20px;
    backdrop-filter: blur(15px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    .section-title {
      color: white;
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 15px;
    }
    
    .section-content {
      color: rgba(255, 255, 255, 0.9);
      font-size: 16px;
      line-height: 1.6;
      margin-bottom: 20px;
    }
    
    .test-image {
      img {
        width: 100%;
        height: 200px;
        object-fit: cover;
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
    }
  }
}

.bottom-tip {
  text-align: center;
  margin-top: 40px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  backdrop-filter: blur(15px);
  
  p {
    color: white;
    font-size: 18px;
    margin-bottom: 20px;
  }
  
  .back-btn {
    height: 48px;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 500;
  }
}

@media (max-width: 768px) {
  .content {
    padding: 15px;
  }
  
  .header .title {
    font-size: 28px;
  }
  
  .test-sections .test-section {
    padding: 20px;
    
    .section-title {
      font-size: 20px;
    }
  }
}
</style> 