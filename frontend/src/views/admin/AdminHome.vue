<template>
  <div class="admin-home">
    <h1>后台管理</h1>
    
    <div class="stats">
      <div class="stat-card">
        <div class="stat-number">{{ totalDiaries }}</div>
        <div class="stat-label">总回忆数</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ thisMonthDiaries }}</div>
        <div class="stat-label">本月新增</div>
      </div>
    </div>
    
    <div class="actions">
      <van-button 
        type="primary" 
        size="large" 
        @click="$router.push('/admin/diary/create')"
        class="action-btn"
      >
        创建新回忆
      </van-button>
      
      <van-button 
        type="default" 
        size="large" 
        @click="$router.push('/admin/diary/list')"
        class="action-btn"
      >
        管理回忆
      </van-button>
    </div>
    
    <div class="recent-diaries">
      <h3>最近回忆</h3>
      <div class="diary-list">
        <div 
          v-for="diary in recentDiaries" 
          :key="diary.id"
          class="diary-item"
          @click="editDiary(diary)"
        >
          <div class="diary-info">
            <div class="diary-title">{{ diary.title }}</div>
            <div class="diary-date">{{ formatDate(diary.date) }}</div>
          </div>
          <van-icon name="edit" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'
import { getAdminStats, getRecentDiaries } from '@/api/admin.js'

const router = useRouter()

const totalDiaries = ref(0)
const thisMonthDiaries = ref(0)
const recentDiaries = ref([])

// 加载统计数据
const loadStats = async () => {
  try {
    const stats = await getAdminStats()
    totalDiaries.value = stats.totalDiaries
    thisMonthDiaries.value = stats.thisMonthDiaries
  } catch (error) {
    console.error('加载统计数据失败:', error)
    showToast('加载统计数据失败')
  }
}

// 加载最近日记
const loadRecentDiaries = async () => {
  try {
    const diaries = await getRecentDiaries(5)
    recentDiaries.value = diaries
  } catch (error) {
    console.error('加载最近日记失败:', error)
    showToast('加载最近日记失败')
  }
}

onMounted(() => {
  loadStats()
  loadRecentDiaries()
})

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const editDiary = (diary) => {
  router.push(`/admin/diary/edit/${diary.id}`)
}
</script>

<style scoped>
.admin-home {
  h1 {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
    background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    font-size: 32px;
    font-weight: bold;
  }
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
  
  .stat-card {
    background: white;
    padding: 20px;
    border-radius: 12px;
    text-align: center;
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
      background: linear-gradient(90deg, transparent, rgba(255, 107, 157, 0.1), transparent);
      transition: left 0.5s;
    }
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 25px rgba(255, 107, 157, 0.2);
      
      &::before {
        left: 100%;
      }
    }
    
    .stat-number {
      font-size: 32px;
      font-weight: bold;
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 8px;
    }
    
    .stat-label {
      color: #666;
      font-size: 14px;
    }
  }
}

.actions {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  
  .action-btn {
    flex: 1;
    height: 48px;
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
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
      transition: left 0.5s;
    }
    
    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
      
      &::before {
        left: 100%;
      }
    }
    
    &:first-child {
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      border: none;
    }
  }
}

.recent-diaries {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
  
  h3 {
    margin-bottom: 15px;
    color: #333;
    background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    font-weight: bold;
  }
  
  .diary-list {
    .diary-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 15px 0;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
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
        background: linear-gradient(90deg, transparent, rgba(255, 107, 157, 0.1), transparent);
        transition: left 0.5s;
      }
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        background: linear-gradient(135deg, rgba(255, 107, 157, 0.05) 0%, rgba(255, 138, 171, 0.05) 100%);
        padding-left: 10px;
        padding-right: 10px;
        margin: 0 -10px;
        border-radius: 8px;
        transform: translateX(5px);
        
        &::before {
          left: 100%;
        }
        
        .van-icon {
          color: #ff6b9d;
          transform: translateX(3px);
        }
      }
      
      .diary-info {
        .diary-title {
          font-size: 16px;
          color: #333;
          margin-bottom: 5px;
          font-weight: 500;
        }
        
        .diary-date {
          font-size: 14px;
          color: #666;
        }
      }
      
      .van-icon {
        color: #999;
        transition: all 0.3s ease;
      }
    }
  }
}
</style> 