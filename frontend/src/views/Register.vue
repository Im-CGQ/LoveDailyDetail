<template>
  <div class="register-page romantic-bg page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <div class="register-container glass-effect">
      <div class="register-header float">
        <div class="logo heartbeat">💕</div>
        <h1 class="title text-gradient-romantic">注册账号</h1>
        <p class="subtitle pulse">开始记录美好回忆</p>
      </div>

      <div class="register-form">
        <van-form @submit="onSubmit">
          <van-field
            v-model="form.username"
            name="username"
            label="用户名"
            placeholder="请输入用户名（3-20个字符）"
            :rules="[
              { required: true, message: '请输入用户名' },
              { min: 3, max: 20, message: '用户名长度必须在3-20个字符之间' }
            ]"
            class="form-field"
          />
          
          <van-field
            v-model="form.displayName"
            name="displayName"
            label="显示名称"
            placeholder="请输入显示名称"
            :rules="[
              { required: true, message: '请输入显示名称' },
              { max: 50, message: '显示名称不能超过50个字符' }
            ]"
            class="form-field"
          />
          
          <van-field
            v-model="form.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码（至少6位）"
            :rules="[
              { required: true, message: '请输入密码' },
              { validator: validatePassword, message: '密码长度至少6位' }
            ]"
            class="form-field"
          />
          
          <van-field
            v-model="form.confirmPassword"
            type="password"
            name="confirmPassword"
            label="确认密码"
            placeholder="请再次输入密码"
            :rules="[
              { required: true, message: '请确认密码' },
              { validator: validateConfirmPassword, message: '两次输入的密码不一致' }
            ]"
            class="form-field"
          />
          
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
              {{ loading ? '注册中...' : '注册' }}
            </van-button>
          </div>
        </van-form>
      </div>

      <div class="register-footer">
        <p class="tip">已有账号？</p>
        <div class="login-link">
          <van-button 
            size="small" 
            type="default"
            @click="goToLogin"
            class="login-btn"
          >
            立即登录
          </van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { register } from '@/api/auth'

const router = useRouter()
const loading = ref(false)

const form = ref({
  username: '',
  displayName: '',
  password: '',
  confirmPassword: ''
})

// 验证密码长度
const validatePassword = (value) => {
  if (!value) return true
  return value.length >= 6
}

// 验证确认密码
const validateConfirmPassword = (value) => {
  return value === form.value.password
}

// 注册提交
const onSubmit = async (values) => {
  console.log('表单提交数据:', values)
  
  // 手动验证密码长度
  if (values.password && values.password.length < 6) {
    showToast({
      message: '密码长度至少6位',
      icon: 'fail'
    })
    return
  }
  
  loading.value = true
  
  try {
    const response = await register(
      values.username,
      values.password,
      values.confirmPassword,
      values.displayName
    )
    
    if (response.success) {
      showToast({
        message: response.message || '注册成功，请登录',
        icon: 'success'
      })
      
      // 跳转到登录页面
      router.push('/login')
    }
  } catch (error) {
    console.error('注册错误详情:', error)
    
    // 显示具体的错误信息
    let errorMessage = '注册失败，请重试'
    
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

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  position: relative;
}

.register-container {
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

.register-header {
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

.register-form {
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

.register-footer {
  text-align: center;
  margin-top: 25px;
  
  .tip {
    color: #999;
    font-size: 14px;
    margin-bottom: 15px;
  }
  
  .login-link {
    .login-btn {
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
  .register-container {
    padding: 30px 25px;
    margin: 10px;
  }
  
  .register-header {
    .logo {
      font-size: 40px;
    }
    
    .title {
      font-size: 24px;
    }
  }
  
  .register-form .submit-btn .van-button {
    height: 48px;
    font-size: 16px;
  }
}
</style> 