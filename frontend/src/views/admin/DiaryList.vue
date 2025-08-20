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
            <img :src="diary.images[0]" :alt="diary.title" class="diary-image" />
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
            @click="deleteDiary(diary)"
          >
            删除
          </van-button>
        </div>
      </div>
    </van-list>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const finished = ref(false)
const diaries = ref([])

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const editDiary = (diary) => {
  router.push(`/admin/diary/edit/${diary.id}`)
}

const deleteDiary = async (diary) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定要删除"${diary.title}"吗？`
    })
    
    // 模拟删除操作
    const index = diaries.value.findIndex(d => d.id === diary.id)
    if (index > -1) {
      diaries.value.splice(index, 1)
      showToast('删除成功')
    }
  } catch {
    // 用户取消删除
  }
}

const onLoad = () => {
  // 模拟加载数据
  setTimeout(() => {
    const newDiaries = [
      {
        id: 1,
        title: '我们的第一次约会',
        date: '2024-01-15',
        description: '今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。',
        images: ['https://picsum.photos/200/150?random=1']
      },
      {
        id: 2,
        title: '情人节特别回忆',
        date: '2024-02-14',
        description: '情人节这天，我们一起去了游乐园，坐了摩天轮，在最高点许下了美好的愿望。',
        images: ['https://picsum.photos/200/150?random=2']
      },
      {
        id: 3,
        title: '春天的野餐',
        date: '2024-03-20',
        description: '春天来了，我们一起去公园野餐，享受阳光和美食，还有彼此的陪伴。',
        images: ['https://picsum.photos/200/150?random=3']
      }
    ]
    
    diaries.value.push(...newDiaries)
    loading.value = false
    finished.value = true
  }, 1000)
}
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
      font-size: 24px;
      font-weight: bold;
    }
    
    .create-btn {
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