<template>
  <div class="edit-profile-page romantic-bg page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <div class="edit-profile-container glass-effect">
      <div class="edit-profile-header float">
        <div class="logo heartbeat">👤</div>
        <h1 class="title text-gradient-romantic">编辑个人信息</h1>
        <p class="subtitle pulse">修改您的个人资料</p>
      </div>

      <div class="edit-profile-form">
        <van-form @submit="handleSubmit">
          <!-- 用户名输入（只读） -->
          <van-field
            v-model="form.username"
            name="username"
            label="用户名"
            placeholder="用户名"
            readonly
            class="form-field disabled"
          />

          <!-- 显示名称输入 -->
          <van-field
            v-model="form.displayName"
            name="displayName"
            label="显示名称"
            placeholder="请输入显示名称"
            :rules="displayNameRules"
            maxlength="50"
            class="form-field"
          />

          <!-- 用户角色（只读） -->
          <van-field
            v-model="form.role"
            name="role"
            label="用户角色"
            :value="getRoleText(form.role)"
            readonly
            class="form-field disabled"
          />

          <!-- 密码修改区域 -->
          <div class="password-section">
            <div class="section-title">修改密码（可选）</div>
            
            <!-- 新密码输入 -->
            <van-field
              v-model="form.password"
              name="password"
              label="新密码"
              placeholder="留空则不修改密码"
              type="password"
              maxlength="20"
              :rules="passwordRules"
              class="form-field"
            />

            <!-- 确认密码输入 -->
            <van-field
              v-model="form.confirmPassword"
              name="confirmPassword"
              label="确认密码"
              placeholder="请再次输入新密码"
              type="password"
              maxlength="20"
              :rules="confirmPasswordRules"
              class="form-field"
            />
          </div>

          <!-- 提交按钮 -->
          <div class="submit-btn">
            <van-button 
              round 
              block 
              type="primary" 
              native-type="submit"
              :loading="loading"
              class="btn-primary"
            >
              <span class="btn-icon">{{ loading ? '⏳' : '💾' }}</span>
              {{ loading ? '保存中...' : '保存修改' }}
            </van-button>
          </div>
        </van-form>
      </div>

      <div class="edit-profile-footer">
        <div class="back-link">
          <van-button 
            size="small" 
            type="default"
            @click="handleCancel"
            class="back-btn"
          >
            返回
          </van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getCurrentUserInfo, updateUserInfo } from '@/api/user.js'

export default {
  name: 'EditProfile',
  setup() {
    const router = useRouter()

    const loading = ref(false)

    const form = reactive({
      username: '',
      displayName: '',
      role: '',
      password: '',
      confirmPassword: ''
    })

    // 验证规则
    const displayNameRules = [
      { required: true, message: '请输入显示名称' },
      { min: 1, max: 50, message: '显示名称长度必须在1-50个字符之间' }
    ]

    const passwordRules = [
      { 
        validator: (value) => {
          if (!value) return true // 密码为空时允许（不修改密码）
          return value.length >= 6 && value.length <= 20
        }, 
        message: '密码长度必须在6-20个字符之间' 
      }
    ]

    const confirmPasswordRules = computed(() => [
      { 
        validator: (value) => {
          if (!form.password) return true // 如果密码为空，确认密码也允许为空
          return value === form.password
        }, 
        message: '两次输入的密码不一致' 
      }
    ])

    const getRoleText = (role) => {
      const roleMap = {
        'ADMIN': '管理员',
        'USER': '普通用户'
      }
      return roleMap[role] || role
    }

    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        loading.value = true
        const response = await getCurrentUserInfo()
        const userData = response.data

        form.username = userData.username
        form.displayName = userData.displayName || ''
        form.role = userData.role
      } catch (error) {
        showToast(error.message || '获取用户信息失败')
        router.push('/login')
      } finally {
        loading.value = false
      }
    }

    // 提交表单
    const handleSubmit = async () => {
      try {
        loading.value = true

        const updateData = {
          displayName: form.displayName.trim()
        }

        // 只有当用户输入了密码时才包含密码字段
        if (form.password) {
          updateData.password = form.password
          updateData.confirmPassword = form.confirmPassword
        }

        await updateUserInfo(updateData)

        showToast('用户信息更新成功')

        // 清空密码字段
        form.password = ''
        form.confirmPassword = ''

        // 返回上一页
        router.back()
      } catch (error) {
        showToast(error.message || '更新用户信息失败')
      } finally {
        loading.value = false
      }
    }

    // 取消编辑
    const handleCancel = () => {
      router.back()
    }

    onMounted(() => {
      loadUserInfo()
    })

    return {
      form,
      loading,
      displayNameRules,
      passwordRules,
      confirmPasswordRules,
      getRoleText,
      handleSubmit,
      handleCancel
    }
  }
}
</script>

<style lang="scss" scoped>
.edit-profile-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  position: relative;
}

.edit-profile-container {
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

.edit-profile-header {
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

.edit-profile-form {
  .form-field {
    margin-bottom: 25px;
    position: relative;
    
    &.disabled {
      opacity: 0.6;
    }
    
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
  }
  
  .password-section {
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #eee;
    
    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 20px;
      text-align: center;
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

.edit-profile-footer {
  text-align: center;
  margin-top: 25px;
  
  .back-link {
    margin-top: 15px;
    
    .back-btn {
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
  .edit-profile-container {
    padding: 30px 25px;
    margin: 10px;
  }
  
  .edit-profile-header {
    .logo {
      font-size: 40px;
    }
    
    .title {
      font-size: 24px;
    }
  }
  
  .edit-profile-form .submit-btn .van-button {
    height: 48px;
    font-size: 16px;
  }
}
</style>
