<template>
  <div class="phone-register-container">
    <div class="register-header">
      <van-icon name="arrow-left" @click="goBack" class="back-icon" />
      <h2>手机号注册</h2>
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
        />

        <!-- 显示名称输入 -->
        <van-field
          v-model="form.displayName"
          name="displayName"
          label="显示名称"
          placeholder="请输入显示名称"
          :rules="displayNameRules"
          maxlength="20"
        />

        <!-- 手机号输入 -->
        <van-field
          v-model="form.phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="phoneRules"
          type="tel"
          maxlength="11"
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

        <!-- 密码输入 -->
        <van-field
          v-model="form.password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="passwordRules"
          type="password"
          maxlength="20"
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
        />

        <!-- 注册按钮 -->
        <div class="submit-btn">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="loading"
            size="large"
          >
            注册
          </van-button>
        </div>
      </van-form>

      <!-- 其他注册方式 -->
      <div class="other-register">
        <div class="divider">
          <span>其他注册方式</span>
        </div>
        <div class="register-options">
          <van-button
            type="default"
            size="small"
            @click="$router.push('/email-register')"
          >
            邮箱注册
          </van-button>
        </div>
      </div>

      <!-- 登录链接 -->
      <div class="login-link">
        <span>已有账号？</span>
        <van-button
          type="primary"
          size="small"
          plain
          @click="$router.push('/phone-login')"
        >
          手机号登录
        </van-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { register, sendSmsCode } from '@/api/auth'

export default {
  name: 'PhoneRegister',
  setup() {
    const router = useRouter()

    const loading = ref(false)
    const sending = ref(false)
    const countdown = ref(0)

    const form = reactive({
      username: '',
      displayName: '',
      phone: '',
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

    const phoneRules = [
      { required: true, message: '请输入手机号' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
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
      if (!form.phone) {
        showToast('请先输入手机号')
        return
      }

      if (!/^1[3-9]\d{9}$/.test(form.phone)) {
        showToast('请输入正确的手机号')
        return
      }

      try {
        sending.value = true
        await sendSmsCode(form.phone)
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
        const response = await register(
          form.username,
          form.password,
          form.confirmPassword,
          form.displayName,
          form.phone,
          form.verificationCode
        )
        
        if (response.success) {
          showToast('注册成功，请登录')
          router.push('/phone-login')
        }
      } catch (error) {
        showToast(error.message || '注册失败')
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
      usernameRules,
      displayNameRules,
      phoneRules,
      verificationCodeRules,
      passwordRules,
      confirmPasswordRules,
      sendVerificationCode,
      handleSubmit,
      goBack
    }
  }
}
</script>

<style scoped>
.phone-register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-header {
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

.register-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.register-form {
  background: white;
  border-radius: 16px;
  padding: 30px 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.submit-btn {
  margin: 30px 0 20px;
}

.other-register {
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

.register-options {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link span {
  margin-right: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .phone-register-container {
    padding: 15px;
  }
  
  .register-form {
    padding: 20px 15px;
  }
  
  .register-header h2 {
    font-size: 20px;
  }
}
</style>
