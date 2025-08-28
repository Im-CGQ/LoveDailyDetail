<template>
  <div class="user-profile-card" @click="handleClick">
    <div class="user-avatar">
      <span class="avatar-icon">👤</span>
    </div>
    <div class="user-info">
      <h3 class="user-name">{{ userInfo.displayName || userInfo.username }}</h3>
      <p class="user-role">{{ getRoleText(userInfo.role) }}</p>
    </div>
    <div class="user-actions" v-if="showActions">
      <van-button 
        size="small" 
        type="primary" 
        @click.stop="goToEditProfile"
        class="edit-btn"
      >
        编辑
      </van-button>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

export default {
  name: 'UserProfileCard',
  props: {
    showActions: {
      type: Boolean,
      default: true
    },
    clickable: {
      type: Boolean,
      default: true
    }
  },
  emits: ['click'],
  setup(props, { emit }) {
    const router = useRouter()
    const userStore = useUserStore()
    
    const userInfo = computed(() => userStore.userInfo || {})
    
    const getRoleText = (role) => {
      const roleMap = {
        'ADMIN': '管理员',
        'USER': '普通用户'
      }
      return roleMap[role] || role
    }
    
    const handleClick = () => {
      if (props.clickable) {
        emit('click')
      }
    }
    
    const goToEditProfile = () => {
      router.push('/edit-profile')
    }
    
    return {
      userInfo,
      getRoleText,
      handleClick,
      goToEditProfile
    }
  }
}
</script>

<style scoped>
.user-profile-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 1px solid rgba(255, 107, 157, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-profile-card:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-icon {
  font-size: 20px;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  margin: 0 0 2px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  margin: 0;
  font-size: 12px;
  color: #666;
}

.user-actions {
  flex-shrink: 0;
}

.edit-btn {
  height: 28px;
  padding: 0 12px;
  font-size: 12px;
  border-radius: 14px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
}

.edit-btn:hover {
  background: linear-gradient(135deg, #3e9bed 0%, #00e1ed 100%);
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .user-profile-card {
    padding: 10px;
  }
  
  .user-avatar {
    width: 36px;
    height: 36px;
  }
  
  .avatar-icon {
    font-size: 18px;
  }
  
  .user-name {
    font-size: 13px;
  }
  
  .user-role {
    font-size: 11px;
  }
}
</style>
