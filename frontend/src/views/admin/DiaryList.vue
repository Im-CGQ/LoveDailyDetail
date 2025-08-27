<template>
  <div class="diary-list">
    <div class="header">
      <h2>回忆管理</h2>
      <van-button 
        type="primary" 
        @click="$router.push('/admin/diary/create')"
        class="create-btn"
      >
        创建回忆
      </van-button>
    </div>
    
    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <div 
        v-for="diary in diaries" 
        :key="diary.id"
        class="diary-item"
      >
        <div class="diary-content">
          <div class="diary-info">
            <h3 class="diary-title">{{ diary.title }}</h3>
            <p class="diary-date">{{ formatDate(diary.date) }}</p>
            <p class="diary-desc">{{ diary.description }}</p>
          </div>
          
          <div class="diary-media" v-if="diary.images && diary.images.length > 0">
            <img :src="diary.images[0].imageUrl" :alt="diary.title" class="diary-image" />
          </div>
        </div>
        
        <div class="diary-actions">
          <van-button 
            size="small" 
            type="primary" 
            @click="editDiary(diary)"
          >
            编辑
          </van-button>
          <van-button 
            size="small" 
            type="danger" 
            @click="deleteDiaryHandler(diary)"
          >
            删除
          </van-button>
        </div>
      </div>
    </van-list>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import dayjs from 'dayjs'
import { getDiariesWithPagination, deleteDiary } from '@/api/admin.js'

const router = useRouter()

const loading = ref(false)
const finished = ref(false)
const diaries = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const editDiary = (diary) => {
  router.push(`/admin/diary/edit/${diary.id}`)
}

const deleteDiaryHandler = async (diary) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定要删除"${diary.title}"吗？`
    })
    
    await deleteDiary(diary.id)
    showToast('删除成功')
    
    // 重新加载数据
    diaries.value = []
    currentPage.value = 1
    hasMore.value = true
    finished.value = false
    await loadDiaries(true)
  } catch (error) {
    if (error) {
      console.error('删除失败:', error)
      showToast('删除失败，请重试')
    }
  }
}

const loadDiaries = async (isInitial = false) => {
  try {
    const result = await getDiariesWithPagination(currentPage.value, pageSize.value)
    
    if (isInitial || currentPage.value === 1) {
      diaries.value = result.content
    } else {
      diaries.value.push(...result.content)
    }
    
    hasMore.value = currentPage.value < result.totalPages
    currentPage.value++
    
    if (!hasMore.value) {
      finished.value = true
    }
  } catch (error) {
    console.error('加载日记列表失败:', error)
    showToast('加载失败，请重试')
  }
}

const onLoad = async () => {
  if (!hasMore.value || loading.value) {
    finished.value = true
    return
  }
  
  loading.value = true
  await loadDiaries(false)
  loading.value = false
}

onMounted(() => {
  loadDiaries(true)
})
</script>

<style scoped>
.diary-list {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      color: #333;
      margin: 0;
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      font-size: 18px;
      font-weight: 600;
    }
    
    .create-btn {
      height: 32px;
      padding: 0 16px;
      font-size: 14px;
      border-radius: 16px;
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      border: none;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
      }
    }
  }
}

.diary-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
    background: linear-gradient(90deg, transparent, rgba(255, 107, 157, 0.05), transparent);
    transition: left 0.5s;
  }
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
    
    &::before {
      left: 100%;
    }
  }
  
  .diary-content {
    display: flex;
    gap: 15px;
    margin-bottom: 15px;
    
    .diary-info {
      flex: 1;
      
      .diary-title {
        font-size: 18px;
        color: #333;
        margin-bottom: 8px;
        font-weight: 500;
      }
      
      .diary-date {
        font-size: 14px;
        color: #666;
        margin-bottom: 8px;
      }
      
      .diary-desc {
        font-size: 14px;
        color: #999;
        line-height: 1.5;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
    }
    
    .diary-media {
      .diary-image {
        width: 80px;
        height: 80px;
        object-fit: cover;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.05);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        }
      }
    }
  }
  
  .diary-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
    
    .van-button {
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
      }
    }
  }
}
</style> 