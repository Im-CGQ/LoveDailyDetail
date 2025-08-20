import dayjs from 'dayjs'

// 生成JWT token（已废弃，现在使用后端返回的真实token）
export const generateToken = (username, role, remember = true) => {
  console.warn('generateToken 已废弃，请使用后端返回的真实token')
  return 'deprecated_token'
}

// 验证token（简化版，主要检查是否存在）
export const validateToken = (token) => {
  return token && token.length > 0 && token !== 'deprecated_token'
}

// 保存登录状态
export const saveLoginState = (token, role, username, remember = true) => {
  localStorage.setItem('auth_token', token)
  localStorage.setItem('auth_role', role)
  localStorage.setItem('auth_username', username)
  
  if (remember) {
    localStorage.setItem('auth_remember', 'true')
    localStorage.setItem('auth_expires', dayjs().add(1, 'month').toISOString())
  } else {
    localStorage.setItem('auth_remember', 'false')
    localStorage.setItem('auth_expires', dayjs().add(1, 'day').toISOString())
  }
}

// 检查登录状态
export const checkLoginState = () => {
  const token = localStorage.getItem('auth_token')
  const role = localStorage.getItem('auth_role')
  const expires = localStorage.getItem('auth_expires')
  
  if (!token || !role || !expires) return false
  
  if (dayjs().isAfter(dayjs(expires))) {
    clearLoginState()
    return false
  }
  
  if (!validateToken(token)) {
    clearLoginState()
    return false
  }
  
  return true
}

// 清除登录状态
export const clearLoginState = () => {
  localStorage.removeItem('auth_token')
  localStorage.removeItem('auth_role')
  localStorage.removeItem('auth_username')
  localStorage.removeItem('auth_remember')
  localStorage.removeItem('auth_expires')
  localStorage.removeItem('admin_token') // 兼容旧版本
}

// 获取当前用户信息
export const getCurrentUser = () => {
  if (!checkLoginState()) return null
  
  return {
    username: localStorage.getItem('auth_username'),
    role: localStorage.getItem('auth_role')
  }
}

// 检查是否有权限访问
export const hasPermission = (requiredRole) => {
  const user = getCurrentUser()
  if (!user) return false
  
  return user.role === requiredRole
} 