import dayjs from 'dayjs'

// 生成JWT token（模拟）
export const generateToken = (username, role, remember = true) => {
  const payload = {
    username,
    role,
    exp: remember ? dayjs().add(1, 'month').unix() : dayjs().add(1, 'day').unix(),
    iat: dayjs().unix()
  }
  
  // 简单的base64编码模拟JWT
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }))
  const payloadStr = btoa(JSON.stringify(payload))
  const signature = btoa('love_diary_secret_key_' + Date.now())
  
  return `${header}.${payloadStr}.${signature}`
}

// 验证token
export const validateToken = (token) => {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) return false
    
    const payload = JSON.parse(atob(parts[1]))
    const now = dayjs().unix()
    
    return payload.exp > now
  } catch (error) {
    return false
  }
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