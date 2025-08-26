<template>
  <div class="join-room">
    <BackButton />
    
    <div class="content">
      <div class="join-form">
        <h1 class="title">🎬 加入电影房间</h1>
        <p class="subtitle">输入房间码，和心爱的人一起看电影</p>
        
        <div class="form-group">
          <label class="form-label">房间码</label>
          <input 
            v-model="roomCode" 
            type="text" 
            class="form-input"
            placeholder="请输入6位房间码"
            maxlength="6"
            @keyup.enter="joinRoom"
          />
        </div>
        
        <button 
          class="join-btn" 
          @click="joinRoom"
          :disabled="!roomCode || joining"
        >
          {{ joining ? '加入中...' : '加入房间' }}
        </button>
        
        <div class="tips">
          <h3>💡 使用提示</h3>
          <ul>
            <li>房间码由创建房间的人提供</li>
            <li>房间码为6位数字</li>
            <li>只有有权限的用户才能加入房间</li>
            <li>加入后可以一起控制播放进度</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import BackButton from '@/components/BackButton.vue'
import { joinRoom as joinRoomApi } from '@/api/movieRoom.js'

const router = useRouter()

const roomCode = ref('')
const joining = ref(false)

const joinRoom = async () => {
  if (!roomCode.value || roomCode.value.length !== 6) {
    showToast('请输入6位房间码')
    return
  }
  
  joining.value = true
  try {
    await joinRoomApi(roomCode.value)
    showToast('成功加入房间')
    router.push(`/movie-room/${roomCode.value}`)
  } catch (error) {
    showToast(error.message)
  } finally {
    joining.value = false
  }
}
</script>

<style scoped>
.join-room {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.content {
  max-width: 500px;
  margin: 0 auto;
  padding-top: 60px;
}

.join-form {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  backdrop-filter: blur(10px);
  text-align: center;
}

.title {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  color: #666;
  margin-bottom: 30px;
  font-size: 16px;
}

.form-group {
  margin-bottom: 25px;
  text-align: left;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 15px;
  border: 2px solid #ddd;
  border-radius: 10px;
  font-size: 18px;
  text-align: center;
  letter-spacing: 2px;
  font-family: monospace;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.join-btn {
  width: 100%;
  padding: 15px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 30px;
}

.join-btn:hover:not(:disabled) {
  background: #5a6fd8;
  transform: translateY(-2px);
}

.join-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.tips {
  text-align: left;
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
}

.tips h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 16px;
}

.tips ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips li {
  color: #666;
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
}

.tips li:before {
  content: '•';
  color: #667eea;
  position: absolute;
  left: 0;
  font-weight: bold;
}

@media (max-width: 768px) {
  .join-form {
    padding: 30px 20px;
  }
  
  .title {
    font-size: 1.5rem;
  }
}
</style>

