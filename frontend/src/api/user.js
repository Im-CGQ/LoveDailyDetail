import request from './request'

// 获取当前用户信息
export function getCurrentUserInfo() {
  return request({
    url: '/api/auth/profile',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/api/auth/profile',
    method: 'put',
    data
  })
}

// 获取用户统计信息
export function getUserStats() {
  return request({
    url: '/api/auth/stats',
    method: 'get'
  })
}
