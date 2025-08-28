<template>
  <div class="register-page romantic-bg page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <div class="register-container glass-effect">
      <div class="register-header float">
        <div class="logo heartbeat">💕</div>
        <h1 class="title text-gradient-romantic">美好回忆</h1>
        <p class="subtitle pulse">创建您的专属账号</p>
      </div>

      <div class="register-form">
        <van-form @submit="handleSubmit">
          <!-- 用户名输入 -->
          <van-field
            v-model="form.username"
            name="username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="usernameRules"
            maxlength="20"
            class="form-field"
          />

          <!-- 显示名称输入 -->
          <van-field
            v-model="form.displayName"
            name="displayName"
            label="显示名称"
            placeholder="请输入显示名称"
            :rules="displayNameRules"
            maxlength="20"
            class="form-field"
          />

          <!-- 邮箱输入 -->
          <van-field
            v-model="form.email"
            name="email"
            label="邮箱"
            placeholder="请输入邮箱"
            :rules="emailRules"
            type="email"
            class="form-field"
          />

          <!-- 验证码输入 -->
          <van-field
            v-model="form.verificationCode"
            name="verificationCode"
            label="验证码"
            placeholder="请输入验证码"
            :rules="verificationCodeRules"
            maxlength="6"
            class="form-field"
          >
            <template #button>
              <van-button
                size="small"
                type="primary"
                :disabled="countdown > 0"
                @click="sendVerificationCode"
                :loading="sending"
                class="send-code-btn"
              >
                {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
              </van-button>
            </template>
          </van-field>

          <!-- 密码输入 -->
          <van-field
            v-model="form.password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :rules="passwordRules"
            type="password"
            maxlength="20"
            class="form-field"
          />

          <!-- 确认密码输入 -->
          <van-field
            v-model="form.confirmPassword"
            name="confirmPassword"
            label="确认密码"
            placeholder="请再次输入密码"
            :rules="confirmPasswordRules"
            type="password"
            maxlength="20"
            class="form-field"
          />

          <!-- 注册按钮 -->
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
        <div class="login-link">
          <p class="login-tip">已有账号？</p>
          <van-button 
            size="small" 
            type="default"
            @click="$router.push('/login')"
            class="login-btn"
          >
            立即登录
          </van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { emailRegister, sendEmailCode } from '@/api/auth'

export default {
  name: 'EmailRegister',
  setup() {
    const router = useRouter()

    const loading = ref(false)
    const sending = ref(false)
    const countdown = ref(0)

    const form = reactive({
      username: '',
      displayName: '',
      email: '',
      verificationCode: '',
      password: '',
      confirmPassword: ''
    })

    // 验证规则
    const usernameRules = [
      { required: true, message: '请输入用户名' },
      { min: 2, max: 20, message: '用户名长度为2-20个字符' },
      { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/, message: '用户名只能包含中文、字母、数字和下划线' }
    ]

    const displayNameRules = [
      { required: true, message: '请输入显示名称' },
      { min: 2, max: 20, message: '显示名称长度为2-20个字符' },
      { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_\s]+$/, message: '显示名称只能包含中文、字母、数字、下划线和空格' }
    ]

    const emailRules = [
      { required: true, message: '请输入邮箱' },
      { 
        pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, 
        message: '请输入正确的邮箱格式' 
      }
    ]

    const verificationCodeRules = [
      { required: true, message: '请输入验证码' },
      { pattern: /^\d{6}$/, message: '验证码为6位数字' }
    ]

    const passwordRules = [
      { required: true, message: '请输入密码' },
      { min: 6, max: 20, message: '密码长度为6-20个字符' }
    ]

    const confirmPasswordRules = computed(() => [
      { required: true, message: '请确认密码' },
      {
        validator: (value) => {
          if (value !== form.password) {
            return '两次输入的密码不一致'
          }
          return true
        }
      }
    ])

    // 发送验证码
    const sendVerificationCode = async () => {
      if (!form.email) {
        showToast('请先输入邮箱')
        return
      }

      if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email)) {
        showToast('请输入正确的邮箱格式')
        return
      }

      try {
        sending.value = true
        await sendEmailCode(form.email)
        showToast('验证码发送成功')
        
        // 开始倒计时
        countdown.value = 60
        const timer = setInterval(() => {
          countdown.value--
          if (countdown.value <= 0) {
            clearInterval(timer)
          }
        }, 1000)
      } catch (error) {
        showToast(error.message || '发送失败')
      } finally {
        sending.value = false
      }
    }

    // 提交注册
    const handleSubmit = async () => {
      try {
        loading.value = true
        const response = await emailRegister(
          form.username,
          form.password,
          form.confirmPassword,
          form.displayName,
          form.email,
          form.verificationCode
        )
        
        if (response.success) {
          showToast('注册成功，请登录')
          router.push('/email-login')
        }
      } catch (error) {
        showToast(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      sending,
      countdown,
      usernameRules,
      displayNameRules,
      emailRules,
      verificationCodeRules,
      passwordRules,
      confirmPasswordRules,
      sendVerificationCode,
      handleSubmit
    }
  }
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
  max-width: 480px;
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
    margin-bottom: 25px;
    position: relative;
    
    :deep(.van-cell) {
      padding: 16px 20px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 15px;
      border: 2px solid transparent;
      transition: all 0.3s ease;
      box-shadow: 0 2px 10px rgba(255, 107, 157, 0.1);
      backdrop-filter: blur(10px);
    }
    
    :deep(.van-field--focus .van-cell) {
      border-color: #ff6b9d;
      box-shadow: 0 4px 20px rgba(255, 107, 157, 0.2);
      transform: translateY(-2px);
    }
    
    :deep(.van-field__label) {
      color: #555;
      font-weight: 600;
      font-size: 15px;
      width: 70px;
      flex-shrink: 0;
      margin-right: 15px;
      
      &::after {
        content: '';
      }
    }
    
    :deep(.van-field__control) {
      color: #333;
      font-size: 16px;
      flex: 1;
      font-weight: 500;
      
      &::placeholder {
        color: #bbb;
        font-weight: 400;
      }
    }
    
    :deep(.van-field__body) {
      flex-direction: row;
      align-items: center;
    }
    
    .send-code-btn {
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      border: none;
      color: white;
      font-size: 12px;
      height: 36px;
      border-radius: 18px;
      padding: 0 16px;
      font-weight: 600;
      box-shadow: 0 2px 8px rgba(255, 107, 157, 0.3);
      transition: all 0.3s ease;
      
      &:hover:not(:disabled) {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(255, 107, 157, 0.4);
      }
      
      &:disabled {
        background: #e0e0e0;
        color: #999;
        box-shadow: none;
      }
    }
    

  }
  
  .submit-btn {
    margin-top: 35px;
    
    .van-button {
      height: 55px;
      font-size: 18px;
      font-weight: 700;
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      border: none;
      border-radius: 28px;
      box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
        transition: left 0.5s;
      }
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 10px 30px rgba(255, 107, 157, 0.4);
        
        &::before {
          left: 100%;
        }
      }
      
      &:active {
        transform: translateY(-1px);
      }
      
      .btn-icon {
        margin-right: 10px;
        font-size: 20px;
      }
    }
  }
}

.register-footer {
  text-align: center;
  margin-top: 25px;
  
  .login-link {
    margin-top: 15px;
    
    .login-tip {
      color: #999;
      font-size: 14px;
      margin-bottom: 10px;
    }
    
    .login-btn {
      background: rgba(255, 255, 255, 0.9);
      border: 2px solid rgba(255, 107, 157, 0.3);
      color: #ff6b9d;
      font-size: 14px;
      font-weight: 600;
      border-radius: 20px;
      padding: 8px 20px;
      transition: all 0.3s ease;
      box-shadow: 0 2px 8px rgba(255, 107, 157, 0.1);
      
      &:hover {
        background: rgba(255, 107, 157, 0.1);
        border-color: #ff6b9d;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(255, 107, 157, 0.2);
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
