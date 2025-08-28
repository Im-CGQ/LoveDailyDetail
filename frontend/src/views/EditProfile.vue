<template>
  <div class="edit-profile-page romantic-bg page-container">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <div class="edit-profile-container glass-effect">
             <div class="edit-profile-header float">
       </div>

      <div class="edit-profile-form">
        <van-form @submit="handleSubmit">
                     <!-- 头像上传区域 -->
           <div class="avatar-section">
             <div class="avatar-container">
              <!-- 隐藏的上传组件 -->
              <van-uploader
                v-model="avatarFiles"
                :max-count="1"
                accept="image/*"
                :after-read="afterAvatarRead"
                :before-delete="beforeAvatarDelete"
                @oversize="onAvatarOversize"
                preview-image
                class="hidden-uploader"
                ref="uploaderRef"
              />
              
                             <!-- 可点击的头像预览 -->
               <div class="avatar-preview" @click="triggerUpload">
                 <img 
                   v-if="getAvatarUrl() && !(avatarFiles.length > 0 && avatarFiles[0].status === 'uploading')" 
                   :src="getAvatarUrl()" 
                   alt="用户头像"
                   class="avatar-image"
                   @error="handleImageError"
                 />
                 <div v-else class="avatar-placeholder">
                   <span class="avatar-icon">👤</span>
                 </div>
                
                <!-- 上传提示遮罩 -->
                <div class="avatar-overlay">
                  <div class="upload-hint">
                    <span class="upload-icon">📷</span>
                    <span class="upload-text">点击更换头像</span>
                  </div>
                </div>
                
                                 <!-- 上传进度指示器 -->
                 <div v-if="avatarFiles.length > 0 && avatarFiles[0].status === 'uploading'" class="upload-progress">
                   <div class="upload-loading">
                     <van-loading size="24px" color="#fff"></van-loading>
                     <span class="upload-text">上传中...</span>
                   </div>
                 </div>
              </div>
              
              
            </div>
          </div>

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
               {{ loading ? '保存中...' : '保存修改' }}
             </van-button>
          </div>
        </van-form>
             </div>
     </div>
   </div>
 </template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getCurrentUserInfo, updateUserInfo } from '@/api/user.js'
import { uploadAvatar } from '@/api/upload.js'
import BackButton from '@/components/BackButton.vue'

export default {
  name: 'EditProfile',
  components: {
    BackButton
  },
  setup() {
    const router = useRouter()

    const loading = ref(false)

    const form = reactive({
      username: '',
      displayName: '',
      avatarUrl: '',
      role: '',
      password: '',
      confirmPassword: ''
    })

    // 头像文件列表
    const avatarFiles = ref([])
    
    // 上传组件引用
    const uploaderRef = ref(null)

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

         // 获取头像URL
     const getAvatarUrl = () => {
       // 如果正在上传中，不显示任何图片
       if (avatarFiles.value.length > 0 && avatarFiles.value[0].status === 'uploading') {
         return ''
       }
       // 如果上传完成且有URL，显示上传的图片
       if (avatarFiles.value.length > 0 && avatarFiles.value[0].url && avatarFiles.value[0].status === 'done') {
         return avatarFiles.value[0].url
       }
       // 否则显示用户原有的头像
       return form.avatarUrl
     }

         // 头像文件上传处理
     const afterAvatarRead = async (file) => {
       console.log('afterAvatarRead called:', file)
       if (Array.isArray(file)) {
         for (const singleFile of file) {
           await processAvatarFile(singleFile)
         }
       } else {
         await processAvatarFile(file)
       }
     }

         const processAvatarFile = async (file) => {
       try {
         // 检查文件大小
         if (file.file.size > 5 * 1024 * 1024) {
           showToast('头像大小不能超过5MB')
           const index = avatarFiles.value.findIndex(f => f.file === file.file)
           if (index > -1) {
             avatarFiles.value.splice(index, 1)
           }
           return
         }
         
         // 检查文件类型
         if (!file.file.type.startsWith('image/')) {
           showToast('只能上传图片文件')
           const index = avatarFiles.value.findIndex(f => f.file === file.file)
           if (index > -1) {
             avatarFiles.value.splice(index, 1)
           }
           return
         }
         
         // 清空之前的文件
         avatarFiles.value = []
         
         // 添加新文件
         avatarFiles.value.push(file)
         
         // 显示上传进度
         file.status = 'uploading'
         file.message = '上传中...'
         
         const url = await uploadAvatar(file.file)
         file.url = url
         file.fileName = file.file.name
         file.status = 'done'
         file.message = '上传成功'
         
         // 更新表单中的头像URL
         form.avatarUrl = url
         
         showToast('头像上传成功')
       } catch (error) {
         console.error('头像上传失败:', error)
         file.status = 'failed'
         file.message = '上传失败'
         showToast('头像上传失败')
         const index = avatarFiles.value.findIndex(f => f.file === file.file)
         if (index > -1) {
           avatarFiles.value.splice(index, 1)
         }
       }
     }

         const beforeAvatarDelete = (file) => {
       console.log('beforeAvatarDelete called:', file)
       return new Promise(resolve => {
         // 清空文件列表
         avatarFiles.value = []
         resolve(true)
       })
     }

         const onAvatarOversize = (file) => {
       showToast('头像大小不能超过5MB')
       return false
     }

     // 处理图片加载错误
     const handleImageError = (event) => {
       console.log('图片加载失败，显示占位符')
       // 图片加载失败时，隐藏图片元素，显示占位符
       event.target.style.display = 'none'
     }

         // 触发文件上传
     const triggerUpload = () => {
       console.log('triggerUpload called')
       if (uploaderRef.value) {
         // 清空当前文件列表，确保能重新选择
         avatarFiles.value = []
         
         // 尝试多种方式找到文件输入框
         const fileInput = uploaderRef.value.$el?.querySelector('input[type="file"]') || 
                          uploaderRef.value.$el?.querySelector('.van-uploader__input') ||
                          uploaderRef.value.$el?.querySelector('input')
         
         if (fileInput) {
           // 重置input的value，确保能选择相同文件
           fileInput.value = ''
           fileInput.click()
         } else {
           // 如果找不到输入框，显示提示
           showToast('请选择图片文件')
         }
       }
     }

    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        loading.value = true
        const response = await getCurrentUserInfo()
        const userData = response.data

        form.username = userData.username
        form.displayName = userData.displayName || ''
        form.avatarUrl = userData.avatarUrl || ''
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

        // 如果上传了新的头像，包含头像URL
        if (avatarFiles.value.length > 0 && avatarFiles.value[0].url) {
          updateData.avatarUrl = avatarFiles.value[0].url
        }

        // 只有当用户输入了密码时才包含密码字段
        if (form.password) {
          updateData.password = form.password
          updateData.confirmPassword = form.confirmPassword
        }

                 await updateUserInfo(updateData)

         showToast('用户信息更新成功')

         // 清空密码字段和头像文件
         form.password = ''
         form.confirmPassword = ''
         avatarFiles.value = []

         // 重新加载用户信息以获取最新的头像URL
         await loadUserInfo()

         // 返回上一页
         router.back()
      } catch (error) {
        showToast(error.message || '更新用户信息失败')
      } finally {
        loading.value = false
      }
    }

         onMounted(() => {
      loadUserInfo()
    })

         return {
       form,
       loading,
       avatarFiles,
       uploaderRef,
       displayNameRules,
       passwordRules,
       confirmPasswordRules,
       getRoleText,
       getAvatarUrl,
       afterAvatarRead,
       beforeAvatarDelete,
       onAvatarOversize,
       handleImageError,
       triggerUpload,
       handleSubmit
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
   padding-top: 80px;
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
 }

 .edit-profile-form {
   padding: 30px;
   
        .avatar-section {
       margin-bottom: 30px;
       padding: 20px;
       background: rgba(255, 255, 255, 0.6);
       border-radius: 15px;
       border: 1px solid rgba(255, 107, 157, 0.1);
    
    .avatar-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 20px;
      text-align: center;
    }
    
    .avatar-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 15px;
      
      .hidden-uploader {
        display: none;
      }
      
             .avatar-preview {
         position: relative;
         cursor: pointer;
         transition: all 0.3s ease;
         width: 100px;
         height: 100px;
         
         &:hover {
           transform: scale(1.05);
           
           .avatar-overlay {
             opacity: 1;
           }
         }
        
                 .avatar-image {
           width: 100px;
           height: 100px;
           border-radius: 50%;
           object-fit: cover;
           box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
           transition: all 0.3s ease;
         }
        
                 .avatar-placeholder {
           width: 100px;
           height: 100px;
           border-radius: 50%;
           background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
           display: flex;
           align-items: center;
           justify-content: center;
           box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
           transition: all 0.3s ease;
          
          .avatar-icon {
            font-size: 40px;
            color: white;
          }
        }
        
        .avatar-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.6);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          opacity: 0;
          transition: opacity 0.3s ease;
          
          .upload-hint {
            text-align: center;
            color: white;
            
            .upload-icon {
              display: block;
              font-size: 24px;
              margin-bottom: 4px;
            }
            
            .upload-text {
              font-size: 12px;
              font-weight: 500;
            }
          }
        }
        
                 .upload-progress {
           position: absolute;
           top: 0;
           left: 0;
           right: 0;
           bottom: 0;
           background: rgba(0, 0, 0, 0.7);
           border-radius: 50%;
           display: flex;
           align-items: center;
           justify-content: center;
           z-index: 10;
           pointer-events: none;
           
           .upload-loading {
             display: flex;
             flex-direction: column;
             align-items: center;
             gap: 8px;
             
             .upload-text {
               color: white;
               font-size: 12px;
               font-weight: 500;
             }
           }
         }
      }
      
      
    }
  }

     .form-field {
     margin-bottom: 25px;
     position: relative;
     
     &.disabled {
       opacity: 0.6;
     }
     
            :deep(.van-cell) {
         padding: 16px 20px;
         background: rgba(255, 255, 255, 0.7);
         border-radius: 12px;
         border: 1px solid rgba(0, 0, 0, 0.05);
         transition: all 0.3s ease;
         box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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
     border-top: 1px solid rgba(0, 0, 0, 0.08);
     
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
      
      
    }
  }
}



 @media (max-width: 768px) {
   .edit-profile-page {
     padding-top: 70px;
   }
   
   .edit-profile-container {
     padding: 20px 15px;
     margin: 10px;
   }
   
   .edit-profile-form {
     padding: 20px;
   }
  
     .edit-profile-header {
     .title {
       font-size: 24px;
     }
   }
  
  .edit-profile-form {
    .avatar-section {
      .avatar-container {
        flex-direction: column;
        gap: 15px;
        
                 .avatar-preview {
           width: 80px;
           height: 80px;
           
           .avatar-image,
           .avatar-placeholder {
             width: 80px;
             height: 80px;
           }
          
          .avatar-placeholder .avatar-icon {
            font-size: 32px;
          }
          
          .avatar-overlay .upload-hint {
            .upload-icon {
              font-size: 20px;
            }
            
            .upload-text {
              font-size: 10px;
            }
          }
        }
      }
    }
    
    .submit-btn .van-button {
      height: 48px;
      font-size: 16px;
    }
  }
}
</style>
