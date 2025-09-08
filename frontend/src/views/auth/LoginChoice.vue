<template>
  <div class="login-choice-container">
    <div class="choice-header">
      <h2>选择登录方式</h2>
      <p>请选择您喜欢的登录方式</p>
    </div>

    <div class="choice-options">
      <!-- 用户名登录 -->
      <div class="choice-card" @click="$router.push('/username-login')">
        <div class="card-icon">
          <van-icon name="user-o" size="32" />
        </div>
        <div class="card-content">
          <h3>用户名登录</h3>
          <p>使用用户名和密码登录</p>
        </div>
        <div class="card-arrow">
          <van-icon name="arrow" />
        </div>
      </div>

      <!-- 手机号登录 -->
      <div class="choice-card" @click="$router.push('/phone-login')">
        <div class="card-icon">
          <van-icon name="phone-o" size="32" />
        </div>
        <div class="card-content">
          <h3>手机号登录</h3>
          <p>使用手机号和验证码登录</p>
        </div>
        <div class="card-arrow">
          <van-icon name="arrow" />
        </div>
      </div>

      <!-- 邮箱登录 -->
      <div class="choice-card" @click="$router.push('/email-login')">
        <div class="card-icon">
          <van-icon name="envelop-o" size="32" />
        </div>
        <div class="card-content">
          <h3>邮箱登录</h3>
          <p>使用邮箱和验证码登录</p>
        </div>
        <div class="card-arrow">
          <van-icon name="arrow" />
        </div>
      </div>
    </div>

    <!-- 注册链接 -->
    <div class="register-section">
      <div class="divider">
        <span>还没有账号？</span>
      </div>
      <div class="register-options">
        <van-button
          type="primary"
          size="large"
          @click="$router.push('/phone-register')"
        >
          手机号注册
        </van-button>
        <van-button
          type="default"
          size="large"
          @click="$router.push('/email-register')"
        >
          邮箱注册
        </van-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginChoice',
  setup() {
    // 组件逻辑
  }
}
</script>

<style scoped>
.login-choice-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.choice-header {
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.choice-header h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.choice-header p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.choice-options {
  margin-bottom: 40px;
}

.choice-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.choice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.card-icon {
  margin-right: 20px;
  color: #667eea;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.card-content p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.card-arrow {
  color: #999;
}

.register-section {
  margin-top: auto;
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
  background: rgba(255, 255, 255, 0.3);
}

.divider span {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0 15px;
  color: white;
  font-size: 14px;
}

.register-options {
  display: flex;
  gap: 15px;
}

.register-options .van-button {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-choice-container {
    padding: 15px;
  }
  
  .choice-header h2 {
    font-size: 24px;
  }
  
  .register-options {
    flex-direction: column;
  }
}
</style>
