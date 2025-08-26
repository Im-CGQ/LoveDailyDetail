<template>
  <div class="back-button" @click="goBack">
    <van-icon name="arrow-left" />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 返回上一页
const goBack = () => {
  router.back()
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
