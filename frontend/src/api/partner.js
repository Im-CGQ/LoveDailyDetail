import api from './http.js'

// 获取伴侣信息
export const getPartnerInfo = async () => {
  try {
    const response = await api.get('/partner/info')
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '获取伴侣信息失败')
    }
  } catch (error) {
    console.error('获取伴侣信息失败:', error.message)
    throw new Error(error.message || '获取伴侣信息失败，请检查网络连接')
  }
}

// 邀请伴侣
export const invitePartner = async (targetUsername) => {
  try {
    const response = await api.post('/partner/invite', { targetUsername })
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '邀请伴侣失败')
    }
  } catch (error) {
    console.error('邀请伴侣失败:', error.message)
    throw new Error(error.message || '邀请伴侣失败，请检查网络连接')
  }
}

// 接受邀请
export const acceptInvitation = async (invitationId) => {
  try {
    const response = await api.post(`/partner/accept/${invitationId}`)
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '接受邀请失败')
    }
  } catch (error) {
    console.error('接受邀请失败:', error.message)
    throw new Error(error.message || '接受邀请失败，请检查网络连接')
  }
}

// 拒绝邀请
export const rejectInvitation = async (invitationId) => {
  try {
    const response = await api.post(`/partner/reject/${invitationId}`)
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '拒绝邀请失败')
    }
  } catch (error) {
    console.error('拒绝邀请失败:', error.message)
    throw new Error(error.message || '拒绝邀请失败，请检查网络连接')
  }
}

// 解除伴侣关系
export const unbindPartner = async () => {
  try {
    const response = await api.post('/partner/unbind')
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '解除伴侣关系失败')
    }
  } catch (error) {
    console.error('解除伴侣关系失败:', error.message)
    throw new Error(error.message || '解除伴侣关系失败，请检查网络连接')
  }
}

// 取消邀请
export const cancelInvitation = async (invitationId) => {
  try {
    const response = await api.post(`/partner/cancel/${invitationId}`)
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '取消邀请失败')
    }
  } catch (error) {
    console.error('取消邀请失败:', error.message)
    throw new Error(error.message || '取消邀请失败，请检查网络连接')
  }
}
