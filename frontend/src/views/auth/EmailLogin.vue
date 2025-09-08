<template>
  <div class="email-login-container">
    <div class="login-header">
      <van-icon name="arrow-left" @click="goBack" class="back-icon" />
      <h2>邮箱登录</h2>
    </div>

    <div class="login-form">
      <van-form @submit="handleSubmit">
        <!-- 邮箱输入 -->
        <van-field
          v-model="form.email"
          name="email"
          label="邮箱"
          placeholder="请输入邮箱"
          :rules="emailRules"
          type="email"
        />

        <!-- 验证码输入 -->
        <van-field
          v-model="form.verificationCode"
          name="verificationCode"
          label="验证码"
          placeholder="请输入验证码"
          :rules="verificationCodeRules"
          maxlength="6"
        >
          <template #button>
            <van-button
              size="small"
              type="primary"
              :disabled="countdown > 0"
              @click="sendVerificationCode"
              :loading="sending"
            >
              {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
            </van-button>
          </template>
        </van-field>

        <!-- 登录按钮 -->
        <div class="submit-btn">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="loading"
            size="large"
          >
            登录
          </van-button>
        </div>
      </van-form>

      <!-- 其他登录方式 -->
      <div class="other-login">
        <div class="divider">
          <span>其他登录方式</span>
        </div>
        <div class="login-options">
          <van-button
            type="default"
            size="small"
            @click="$router.push('/username-login')"
          >
            用户名登录
          </van-button>
          <van-button
            type="default"
            size="small"
            @click="$router.push('/phone-login')"
          >
            手机号登录
          </van-button>
        </div>
      </div>

      <!-- 注册链接 -->
      <div class="register-link">
        <span>还没有账号？</span>
        <van-button
          type="primary"
          size="small"
          plain
          @click="$router.push('/email-register')"
        >
          邮箱注册
        </van-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { emailLogin, sendEmailCode } from '@/api/auth'
import { useUserStore } from '@/stores/user'

export default {
  name: 'EmailLogin',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()

    const loading = ref(false)
    const sending = ref(false)
    const countdown = ref(0)

    const form = reactive({
      email: '',
      verificationCode: ''
    })

    // 验证规则
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

    // 提交登录
    const handleSubmit = async () => {
      try {
        loading.value = true
        const response = await userStore.userEmailLogin(form.email, form.verificationCode)
        
        if (response.success) {
          showToast('登录成功')
          
          // 跳转到首页
          router.push('/')
        }
      } catch (error) {
        showToast(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }

    // 返回上一页
    const goBack = () => {
      router.back()
    }

    return {
      form,
      loading,
      sending,
      countdown,
      emailRules,
      verificationCodeRules,
      sendVerificationCode,
      handleSubmit,
      goBack
    }
  }
}
</script>

<style scoped>
.email-login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-header {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
  color: white;
}

.back-icon {
  font-size: 20px;
  margin-right: 15px;
  cursor: pointer;
}

.login-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.login-form {
  background: white;
  border-radius: 16px;
  padding: 30px 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.submit-btn {
  margin: 30px 0 20px;
}

.other-login {
  margin: 30px 0;
}

.divider {
  text-align: center;
  position: relative;
  margin: 20px 0;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e8e8e8;
}

.divider span {
  background: white;
  padding: 0 15px;
  color: #999;
  font-size: 14px;
}

.login-options {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.register-link span {
  margin-right: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .email-login-container {
    padding: 15px;
  }
  
  .login-form {
    padding: 20px 15px;
  }
  
  .login-header h2 {
    font-size: 20px;
  }
}
</style>
