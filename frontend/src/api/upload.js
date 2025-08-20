import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000
})

/**
 * 获取OSS上传签名
 * @param {string} fileName 文件名
 * @param {string} fileType 文件类型 (image, video, music)
 * @returns {Promise} 签名信息
 */
export const getOssSignature = async (fileName, fileType) => {
  try {
    const response = await api.get('/admin/oss/signature', {
      params: { fileName, fileType }
    })
    return response.data.data
  } catch (error) {
    console.error('获取OSS签名失败:', error)
    throw new Error('获取上传签名失败')
  }
}

/**
 * 直接上传文件到阿里云OSS
 * @param {File} file 文件对象
 * @param {string} fileType 文件类型
 * @returns {Promise<string>} 文件URL
 */
export const uploadToOss = async (file, fileType) => {
  try {
    // 1. 获取OSS上传签名
    const signature = await getOssSignature(file.name, fileType)
    
    // 2. 构建FormData
    const formData = new FormData()
    formData.append('key', signature.fileName)
    formData.append('policy', signature.policy)
    formData.append('OSSAccessKeyId', signature.accessid)
    formData.append('signature', signature.signature)
    formData.append('success_action_status', '200')
    formData.append('file', file)
    
    // 调试信息
    console.log('上传参数:', {
      key: signature.fileName,
      policy: signature.policy,
      OSSAccessKeyId: signature.accessid,
      signature: signature.signature,
      host: signature.host
    })
    
    // 3. 直接上传到OSS
    const response = await axios.post(signature.host, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 60000
    })
    
    // 4. 返回文件URL
    return signature.urlPrefix + '/' + signature.fileName
  } catch (error) {
    console.error('上传文件失败:', error)
    throw new Error('文件上传失败')
  }
}

/**
 * 上传图片
 * @param {File} file 图片文件
 * @returns {Promise<string>} 图片URL
 */
export const uploadImage = async (file) => {
  return uploadToOss(file, 'image')
}

/**
 * 上传视频
 * @param {File} file 视频文件
 * @returns {Promise<string>} 视频URL
 */
export const uploadVideo = async (file) => {
  return uploadToOss(file, 'video')
}

/**
 * 上传音乐
 * @param {File} file 音乐文件
 * @returns {Promise<string>} 音乐URL
 */
export const uploadMusic = async (file) => {
  return uploadToOss(file, 'music')
} 