<template>
  <div class="login-page romantic-bg page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <div class="login-container glass-effect">
      <div class="login-header float">
        <div class="logo heartbeat">💕</div>
        <h1 class="title text-gradient-romantic">{{ isAdmin ? '后台管理' : '美好回忆' }}</h1>
        <p class="subtitle pulse">记录和女朋友的每一天</p>
      </div>

      <div class="login-form">
        <van-form @submit="onSubmit">
          <van-field
            v-model="form.username"
            name="username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请输入用户名' }]"
            class="form-field"
          />
          <van-field
            v-model="form.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请输入密码' }]"
            class="form-field"
          />
          
          <div class="form-options">
            <van-checkbox v-model="rememberMe" class="remember-checkbox">
              记住我一个月
            </van-checkbox>
          </div>
          
          <div class="submit-btn">
            <van-button 
              round 
              block 
              type="primary" 
              native-type="submit"
              :loading="loading"
              class="btn-primary ripple"
            >
              <span class="btn-icon">{{ loading ? '⏳' : '💕' }}</span>
              {{ loading ? '登录中...' : '登录' }}
            </van-button>
          </div>
        </van-form>
      </div>

      <div class="login-footer">
        <div class="mode-switch">
          <van-button 
            size="small" 
            :type="isAdmin ? 'default' : 'primary'"
            @click="switchMode"
            class="switch-btn"
          >
            {{ isAdmin ? '切换到前台' : '切换到后台' }}
          </van-button>
        </div>
        <!-- <div class="register-link" v-if="!isAdmin">
          <p class="register-tip">还没有账号？</p>
          <van-button 
            size="small" 
            type="default"
            @click="goToRegister"
            class="register-btn"
          >
            立即注册
          </van-button>
        </div> -->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import { generateToken, saveLoginState, checkLoginState, clearLoginState } from '@/utils/auth'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const rememberMe = ref(true)
const isAdmin = ref(false)

const form = ref({
  username: '',
  password: ''
})

// 切换登录模式
const switchMode = () => {
  isAdmin.value = !isAdmin.value
  form.value = { username: '', password: '' }
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}



// 登录提交
const onSubmit = async (values) => {
  loading.value = true
  
  try {
    const userStore = useUserStore()
    const response = await userStore.userLogin(values.username, values.password, rememberMe.value)
    
    showToast({
      message: '登录成功',
      icon: 'success'
    })
    
    // 检查是否有保存的重定向路径
    const redirectPath = localStorage.getItem('redirect_after_login')
    if (redirectPath && redirectPath !== '/login') {
      localStorage.removeItem('redirect_after_login')
      router.push(redirectPath)
    } else {
      // 登录后统一进入欢迎页
      router.push('/')
    }
  } catch (error) {
    console.error('登录错误详情:', error)
    
    // 显示具体的错误信息
    let errorMessage = '登录失败，请重试'
    
    if (error.message) {
      errorMessage = error.message
    } else if (error.response && error.response.data && error.response.data.message) {
      errorMessage = error.response.data.message
    }
    
    showToast({
      message: errorMessage,
      icon: 'fail',
      duration: 3000
    })
  } finally {
    loading.value = false
  }
}



// 初始化
onMounted(() => {
  if (checkLoginState()) {
    // 已登录访问登录页时，统一进入欢迎页
    router.push('/')
    return
  }
  
  if (route.query.mode === 'admin') {
    isAdmin.value = true
  }
})
</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  position: relative;
}

.login-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 25px;
  padding: 40px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  z-index: 10;
}

.login-header {
  text-align: center;
  margin-bottom: 35px;
  
  .logo {
    font-size: 48px;
    margin-bottom: 15px;
    display: block;
  }
  
  .title {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 10px;
  }
  
  .subtitle {
    color: #666;
    font-size: 16px;
    opacity: 0.8;
  }
}

.login-form {
  .form-field {
    margin-bottom: 20px;
    
    :deep(.van-field__label) {
      color: #333;
      font-weight: 500;
      font-size: 16px;
    }
    
    :deep(.van-field__control) {
      color: #333;
      font-size: 16px;
    }
  }
  
  .form-options {
    margin: 20px 0;
    
    .remember-checkbox {
      :deep(.van-checkbox__label) {
        color: #666;
        font-size: 14px;
      }
    }
  }
  
  .submit-btn {
    margin-top: 30px;
    
    .van-button {
      height: 50px;
      font-size: 18px;
      font-weight: 600;
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      border: none;
      box-shadow: 0 4px 15px rgba(255, 107, 157, 0.3);
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
      }
      
      .btn-icon {
        margin-right: 8px;
      }
    }
  }
}

.login-footer {
  text-align: center;
  margin-top: 25px;
  
  .tip {
    color: #999;
    font-size: 14px;
    margin-bottom: 15px;
  }
  
  .mode-switch {
    .switch-btn {
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 107, 157, 0.3);
      color: #ff6b9d;
      font-size: 14px;
      
      &:hover {
        background: rgba(255, 107, 157, 0.1);
      }
    }
  }
  
  .register-link {
    margin-top: 15px;
    
    .register-tip {
      color: #999;
      font-size: 14px;
      margin-bottom: 10px;
    }
    
    .register-btn {
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 107, 157, 0.3);
      color: #ff6b9d;
      font-size: 14px;
      
      &:hover {
        background: rgba(255, 107, 157, 0.1);
      }
    }
  }
}

@media (max-width: 768px) {
  .login-container {
    padding: 30px 25px;
    margin: 10px;
  }
  
  .login-header {
    .logo {
      font-size: 40px;
    }
    
    .title {
      font-size: 24px;
    }
  }
  
  .login-form .submit-btn .van-button {
    height: 48px;
    font-size: 16px;
  }
}
</style> 