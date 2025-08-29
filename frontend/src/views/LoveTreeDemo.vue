<template>
  <div class="love-tree-demo">
    <div class="demo-header">
      <h1>樱花树动画演示</h1>
      <p>这是一个浪漫的樱花树动画效果，点击红色种子开始动画</p>
      <van-button type="primary" @click="goBack">返回首页</van-button>
    </div>
    
    <LoveTree />
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import LoveTree from './LoveTree.vue'

export default {
  name: 'LoveTreeDemo',
  components: {
    LoveTree
  },
  setup() {
    const router = useRouter()
    
    const goBack = () => {
      router.push('/')
    }
    
    return {
      goBack
    }
  }
}
</script>

<style lang="scss" scoped>
.love-tree-demo {
  position: relative;
  width: 100%;
  height: 100vh;
}

.demo-header {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  
  h1 {
    color: #ff6b9d;
    margin-bottom: 10px;
    font-size: 24px;
  }
  
  p {
    color: #666;
    margin-bottom: 15px;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .demo-header {
    top: 10px;
    padding: 15px;
    
    h1 {
      font-size: 20px;
    }
    
    p {
      font-size: 12px;
    }
  }
}
</style>
