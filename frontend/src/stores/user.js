import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout } from '@/api/auth'
import { getCurrentUserInfo } from '@/api/user'
import { saveLoginState, clearLoginState, checkLoginState, getCurrentUser } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref(null)
  const token = ref('')
  const isLoggedIn = ref(false)
  const loading = ref(false)

  // 计算属性
  const partnerId = computed(() => userInfo.value?.partnerId || null)
  const userId = computed(() => userInfo.value?.id || null)
  const username = computed(() => userInfo.value?.username || '')
  const role = computed(() => userInfo.value?.role || '')
  const displayName = computed(() => userInfo.value?.displayName || '')

  // 初始化用户状态
  const initUserState = async () => {
    if (checkLoginState()) {
      const storedToken = localStorage.getItem('auth_token')
      
      if (storedToken) {
        token.value = storedToken
        isLoggedIn.value = true
        
        try {
          // 从API获取完整的用户信息
          const response = await getCurrentUserInfo()
          if (response.success && response.data) {
            userInfo.value = response.data
          } else {
            // 如果API调用失败，从localStorage获取基本信息
            const currentUser = getCurrentUser()
            if (currentUser) {
              userInfo.value = {
                id: localStorage.getItem('auth_user_id'),
                username: currentUser.username,
                role: currentUser.role,
                displayName: localStorage.getItem('auth_display_name'),
                partnerId: localStorage.getItem('auth_partner_id'),
                email: localStorage.getItem('auth_email')
              }
            }
          }
        } catch (error) {
          console.error('获取用户信息失败:', error)
          // 如果API调用失败，从localStorage获取基本信息
          const currentUser = getCurrentUser()
          if (currentUser) {
            userInfo.value = {
              id: localStorage.getItem('auth_user_id'),
              username: currentUser.username,
              role: currentUser.role,
              displayName: localStorage.getItem('auth_display_name'),
              partnerId: localStorage.getItem('auth_partner_id'),
              email: localStorage.getItem('auth_email')
            }
          }
        }
      }
    }
  }

  // 登录
  const userLogin = async (username, password, remember = true) => {
    loading.value = true
    try {
      const response = await login(username, password)
      
      if (response.success) {
        const user = response.data.user
        const userToken = response.data.token
        
        // 保存到store
        userInfo.value = user
        token.value = userToken
        isLoggedIn.value = true
        
        // 保存到localStorage
        saveLoginState(userToken, user.role, user.username, remember)
        localStorage.setItem('auth_user_id', user.id)
        localStorage.setItem('auth_display_name', user.displayName || '')
        localStorage.setItem('auth_partner_id', user.partnerId || '')
        localStorage.setItem('auth_email', user.email || '')
        
        return response
      } else {
        throw new Error(response.message || '登录失败')
      }
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 登出
  const userLogout = async () => {
    loading.value = true
    try {
      await logout()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      // 无论后端是否成功，都清除本地状态
      clearUserState()
      loading.value = false
    }
  }

  // 清除用户状态
  const clearUserState = () => {
    userInfo.value = null
    token.value = ''
    isLoggedIn.value = false
    clearLoginState()
  }

  // 更新用户信息
  const updateUserInfo = (newUserInfo) => {
    userInfo.value = { ...userInfo.value, ...newUserInfo }
    
    // 同步更新localStorage
    if (newUserInfo.displayName) {
      localStorage.setItem('auth_display_name', newUserInfo.displayName)
    }
    if (newUserInfo.partnerId) {
      localStorage.setItem('auth_partner_id', newUserInfo.partnerId)
    }
    if (newUserInfo.email) {
      localStorage.setItem('auth_email', newUserInfo.email)
    }
  }

  // 检查是否有伴侣
  const hasPartner = computed(() => {
    return !!userInfo.value?.partnerId
  })

  // 检查是否为管理员
  const isAdmin = computed(() => {
    return userInfo.value?.role === 'admin'
  })

  return {
    // 状态
    userInfo,
    token,
    isLoggedIn,
    loading,
    
    // 计算属性
    partnerId,
    userId,
    username,
    role,
    displayName,
    hasPartner,
    isAdmin,
    
    // 方法
    initUserState,
    userLogin,
    userLogout,
    clearUserState,
    updateUserInfo
  }
})

