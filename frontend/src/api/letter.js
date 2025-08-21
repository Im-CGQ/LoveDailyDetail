import request from './request'

// 创建信件
export function createLetter(data) {
  return request({
    url: '/api/letters',
    method: 'post',
    data
  })
}

// 获取发送的信件列表
export function getSentLetters() {
  return request({
    url: '/api/letters/sent',
    method: 'get'
  })
}

// 获取接收的信件列表
export function getReceivedLetters() {
  return request({
    url: '/api/letters/received',
    method: 'get'
  })
}

// 获取已解锁的信件列表
export function getUnlockedLetters() {
  return request({
    url: '/api/letters/unlocked',
    method: 'get'
  })
}

// 获取未解锁的信件列表
export function getLockedLetters() {
  return request({
    url: '/api/letters/locked',
    method: 'get'
  })
}

// 获取信件详情
export function getLetterById(letterId) {
  return request({
    url: `/api/letters/${letterId}`,
    method: 'get'
  })
}

// 标记信件为已读
export function markAsRead(letterId) {
  return request({
    url: `/api/letters/${letterId}/read`,
    method: 'put'
  })
}

// 删除信件
export function deleteLetter(letterId) {
  return request({
    url: `/api/letters/${letterId}`,
    method: 'delete'
  })
}
