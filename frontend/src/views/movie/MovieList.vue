<template>
  <div class="movie-list">
    <div class="top-bar">
      <BackButton />
      <button class="create-btn" @click="goToCreateMovie">
        ➕ 上传电影
      </button>
    </div>
    
    <div class="content">
      <h1 class="title">🎬 一起看电影</h1>
      
      <div class="search-section">
                 <input 
           v-model="searchKeyword" 
           type="text" 
           placeholder="搜索电影..." 
           class="search-input"
           @input="handleSearchInput"
         />
        
        <div class="filter-tabs">
          <button 
            class="filter-tab" 
            :class="{ active: currentTab === 'all' }"
            @click="switchTab('all')"
          >
            全部
          </button>
          <button 
            class="filter-tab" 
            :class="{ active: currentTab === 'my' }"
            @click="switchTab('my')"
          >
            我的
          </button>
          <button 
            class="filter-tab" 
            :class="{ active: currentTab === 'public' }"
            @click="switchTab('public')"
          >
            公开
          </button>
        </div>
      </div>

      <div v-if="listLoading" class="list-loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="filteredMovies.length === 0" class="empty">
        <div class="empty-content">
          <div class="empty-icon">🎬</div>
          <h3>{{ getEmptyTitle() }}</h3>
          <p>{{ getEmptyMessage() }}</p>
          <button v-if="currentTab === 'my'" class="empty-btn" @click="goToCreateMovie">
            ➕ 上传第一部电影
          </button>
        </div>
      </div>
      
      <div v-else class="movies-grid" :class="{ 'fade-in': !listLoading }">
        <div 
          v-for="movie in filteredMovies" 
          :key="movie.id"
          class="movie-card"
        >
          <div class="movie-cover" @click="viewMovie(movie)">
            <img 
              v-if="movie.coverUrl" 
              :src="movie.coverUrl" 
              :alt="movie.title"
            />
            <div v-else class="cover-placeholder">🎬</div>
          </div>
          
          <div class="movie-info">
            <h3 @click="viewMovie(movie)">{{ movie.title }}</h3>
            <p @click="viewMovie(movie)">{{ movie.description || '暂无描述' }}</p>
            <div class="movie-meta">
              <span v-if="movie.durationSeconds">{{ formatDuration(movie.durationSeconds) }}</span>
              <span :class="movie.isPublic ? 'public' : 'private'">
                {{ movie.isPublic ? '公开' : '私密' }}
              </span>
            </div>
            
            <!-- 按钮区域 -->
            <div class="movie-actions">
              <button class="action-btn play-btn" @click="handleCreateRoom(movie)">
                🎬 进入房间
              </button>
              <button 
                v-if="currentTab === 'my'" 
                class="action-btn edit-btn" 
                @click="editMovie(movie)"
              >
                ✏️ 编辑
              </button>
              <button 
                v-if="currentTab === 'my'" 
                class="action-btn delete-btn" 
                @click="handleDeleteMovie(movie)"
              >
                🗑️ 删除
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import BackButton from '@/components/BackButton.vue'

import { getAllMovies, getMyMovies, getPublicMovies, deleteMovie } from '@/api/movie.js'
import { createRoom, checkUserInMovieRoom } from '@/api/movieRoom.js'

const router = useRouter()

const loading = ref(false)
const listLoading = ref(false) // 专门用于列表加载状态
const currentTab = ref('all')
const searchKeyword = ref('')
const movies = ref([])
const searchTimeout = ref(null)

const filteredMovies = computed(() => {
  let filtered = movies.value
  
  // 根据当前tab过滤
  if (currentTab.value === 'my') {
    // 我的tab显示当前用户上传的所有电影（包括公开和私密的）
    // 这里不需要额外过滤，因为getMyMovies()已经返回了当前用户的电影
    filtered = movies.value
  } else if (currentTab.value === 'public') {
    filtered = movies.value.filter(movie => movie.isPublic)
  }
  // 'all' tab 不需要额外过滤，显示所有电影
  
  // 根据搜索关键词过滤
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase().trim()
    filtered = filtered.filter(movie => 
      movie.title.toLowerCase().includes(keyword) ||
      (movie.description && movie.description.toLowerCase().includes(keyword))
    )
  }
  
  return filtered
})



const loadMovies = async () => {
  listLoading.value = true
  try {
    const data = await getAllMovies()
    movies.value = data
  } catch (error) {
    showToast(error.message)
  } finally {
    listLoading.value = false
  }
}

const switchTab = async (tab) => {
  if (currentTab.value === tab) return // 如果点击的是当前tab，不重复加载
  
  // 先更新tab状态，让UI立即响应
  currentTab.value = tab
  
  // 清空搜索关键词，避免切换tab时搜索状态混乱
  searchKeyword.value = ''
  
  // 显示列表加载状态，只针对列表区域
  listLoading.value = true
  
  try {
    let data
    if (tab === 'my') {
      data = await getMyMovies()
    } else if (tab === 'public') {
      data = await getPublicMovies()
    } else {
      data = await getAllMovies()
    }
    
    // 只更新数据，不触发页面刷新
    movies.value = data
  } catch (error) {
    showToast(error.message || '加载失败')
  } finally {
    listLoading.value = false
  }
}

const viewMovie = (movie) => {
  router.push(`/movie/${movie.id}`)
}



const handleCreateRoom = async (movie) => {
  try {
    // 先检查用户是否已经在该电影的房间中
    try {
      const existingRoom = await checkUserInMovieRoom(movie.id)
      // 如果用户已经在房间中，直接进入该房间
      router.push(`/movie-room/${existingRoom.roomCode}`)
      showToast('已进入现有房间')
    } catch (checkError) {
      // 用户不在房间中，创建新房间
      const roomData = {
        roomName: `观看 ${movie.title}`,
        movieId: movie.id
      }
      
      const room = await createRoom(roomData)
      router.push(`/movie-room/${room.roomCode}`)
    }
  } catch (error) {
    showToast(error.message || '创建房间失败')
  }
}

const handleSearchInput = () => {
  // 清除之前的定时器
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  // 设置新的定时器，300ms后执行搜索
  searchTimeout.value = setTimeout(() => {
    // 搜索是实时的，通过computed属性自动过滤
    // 这里可以添加额外的搜索逻辑，比如高亮搜索结果等
  }, 300)
}

const goToCreateMovie = () => {
  router.push('/create-movie')
}



const editMovie = (movie) => {
  router.push(`/edit-movie/${movie.id}`)
}

const handleDeleteMovie = async (movie) => {
  try {
    // 显示确认对话框
    const confirmed = await showConfirmDialog({
      title: '确认删除',
      message: `确定要删除电影"${movie.title}"吗？此操作不可恢复。`,
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      confirmButtonColor: '#ff4444'
    })
    
    if (confirmed) {
      await deleteMovie(movie.id)
      showToast('删除成功')
      // 重新加载电影列表
      await loadMovies()
    }
  } catch (error) {
    if (error.message !== 'cancel') {
      showToast(error.message || '删除失败')
    }
  }
}

const formatDuration = (duration) => {
  if (!duration || duration <= 0) return '未知'
  
  // duration是秒数，直接使用
  const totalSeconds = Math.round(duration)
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const seconds = totalSeconds % 60
  
  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  } else {
    return `${minutes}:${seconds.toString().padStart(2, '0')}`
  }
}

const getEmptyTitle = () => {
  if (currentTab.value === 'my') {
    return '还没有上传电影'
  } else if (currentTab.value === 'public') {
    return '暂无公开电影'
  } else {
    return '暂无电影'
  }
}

const getEmptyMessage = () => {
  if (currentTab.value === 'my') {
    return '上传你的第一部电影，与伴侣一起观看'
  } else if (currentTab.value === 'public') {
    return '暂时没有公开的电影可以观看'
  } else {
    return '暂时没有电影可以观看'
  }
}

onMounted(() => {
  loadMovies()
})
</script>

<style scoped>
.movie-list {
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.top-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  z-index: 100;
}



.content {
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 80px;
}

.title {
  text-align: center;
  color: white;
  font-size: 2.5rem;
  margin-bottom: 30px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.search-section {
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  padding: 25px;
  margin-bottom: 20px;
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  border: 1px solid rgba(255,255,255,0.2);
}

.search-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #ddd;
  border-radius: 10px;
  font-size: 16px;
  margin-bottom: 15px;
}

.filter-tabs {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.filter-tab {
  padding: 8px 16px;
  border: 2px solid #667eea;
  border-radius: 20px;
  background: transparent;
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tab.active {
  background: #667eea;
  color: white;
}

.create-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #ff6b9d 0%, #f093fb 100%);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position:absolute;
  right:20px;
  top: 25px;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.3);
  white-space: nowrap;
}

.create-btn:hover {
  background: linear-gradient(135deg, #f55a8b 0%, #e085e8 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

.loading {
  text-align: center;
  color: white;
  padding: 40px;
  font-size: 18px;
}

.list-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  padding: 40px;
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  border: 1px solid rgba(255,255,255,0.2);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.list-loading p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.empty {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  padding: 40px;
}

.empty-content {
  text-align: center;
  background: rgba(255,255,255,0.95);
  border-radius: 25px;
  padding: 40px;
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  border: 1px solid rgba(255,255,255,0.2);
  max-width: 400px;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.empty-content h3 {
  color: #333;
  font-size: 24px;
  margin: 0 0 15px 0;
  font-weight: 600;
}

.empty-content p {
  color: #666;
  font-size: 16px;
  margin: 0 0 25px 0;
  line-height: 1.5;
}

.empty-btn {
  padding: 12px 25px;
  background: linear-gradient(135deg, #ff6b9d 0%, #f093fb 100%);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.3);
}

.empty-btn:hover {
  background: linear-gradient(135deg, #f55a8b 0%, #e085e8 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

.movies-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.3s ease;
}

.movies-grid.fade-in {
  opacity: 1;
  transform: translateY(0);
}

.movie-card {
  background: rgba(255,255,255,0.9);
  border-radius: 15px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.movie-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.movie-cover {
  position: relative;
  height: 200px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.movie-cover:hover {
  transform: scale(1.02);
}

.movie-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.movie-info h3 {
  margin: 0 0 10px 0;
  color: #333;
  cursor: pointer;
  transition: color 0.3s ease;
}

.movie-info h3:hover {
  color: #667eea;
}

.movie-info p {
  color: #666;
  margin: 0 0 10px 0;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.movie-info p:hover {
  color: #333;
}

/* 按钮区域样式 */
.movie-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.action-btn {
  padding: 10px 16px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  min-width: 0;
  white-space: nowrap;
}

.play-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.play-btn:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4c93 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.edit-btn {
  background: linear-gradient(135deg, #ff9800 0%, #ff5722 100%);
  color: white;
}

.edit-btn:hover {
  background: linear-gradient(135deg, #f57c00 0%, #e64a19 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
}

.delete-btn {
  background: linear-gradient(135deg, #ff4444 0%, #cc0000 100%);
  color: white;
}

.delete-btn:hover {
  background: linear-gradient(135deg, #e63939 0%, #b30000 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 68, 68, 0.3);
}

.movie-info {
  padding: 15px;
}

.movie-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.public {
  color: #4caf50;
}

.private {
  color: #ff9800;
}



@media (max-width: 768px) {
  .top-bar {
    padding: 10px 15px;
  }
  
  .search-section {
    padding: 20px;
  }
  
  .create-btn {
    padding: 8px 15px;
    font-size: 12px;
  }
  
  .movies-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
  
  .title {
    font-size: 2rem;
  }
  
  .empty-content {
    padding: 30px 20px;
    margin: 0 20px;
  }
  
  .empty-icon {
    font-size: 50px;
  }
  
  .empty-content h3 {
    font-size: 20px;
  }
  
  .empty-content p {
    font-size: 14px;
  }
  
  .movie-actions {
    gap: 8px;
  }
  
  .action-btn {
    padding: 8px 14px;
    font-size: 13px;
  }
}
</style>
