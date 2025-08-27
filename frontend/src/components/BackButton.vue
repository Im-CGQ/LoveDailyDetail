<template>
  <div class="back-button" @click="goBack">
    <van-icon name="arrow-left" />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 智能返回逻辑
const goBack = () => {
  // 检查历史记录长度，如果只有当前页面，说明是直接访问
  if (window.history.length <= 1) {
    // 直接访问，跳转到欢迎页面
    console.log('返回欢迎页');
    router.push('/')
  } else {
    console.log('返回上一页');
    // 有历史记录，返回上一页
    router.back()
  }
}
</script>

<style scoped>
.back-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1000;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: scale(1.1);
  }
  
  .van-icon {
    color: white;
    font-size: 20px;
  }
}
</style>
