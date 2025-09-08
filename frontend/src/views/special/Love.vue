<template>
  <div class="love-page">
    <!-- 音乐控制 -->
    <div class="music-control" @click="toggleMusic">
      <van-icon :name="isPlaying ? 'music-o' : 'volume-cross-o'" />
      <span class="music-text">{{ isPlaying ? '正在播放' : '点击播放音乐' }}</span>
    </div>

    <!-- 返回按钮 -->
    <div class="back-button" @click="goBack">
      <van-icon name="arrow-left" />
      返回
      </div>

    <!-- 粒子层（心形） -->
    <canvas ref="heartCanvas" class="heart-canvas"></canvas>

    <!-- 英雄区 -->
    <section class="hero">
      <div class="hero-inner">
        <div class="profile">
          <div class="avatar-pair">
            <div class="avatar" :style="avatarStyle(profile.avatarA)">{{ initials(profile.nameA) }}</div>
            <div class="heart-mid">❤</div>
            <div class="avatar" :style="avatarStyle(profile.avatarB)">{{ initials(profile.nameB) }}</div>
          </div>
          <div class="names">{{ profile.nameA }} × {{ profile.nameB }}</div>
        </div>
        <h1 class="hero-title">{{ pageTitle }}</h1>
        <p class="hero-subtitle">{{ pageSubtitle }}</p>
        <div class="hero-type">
          <span class="type-cursor">{{ typedText }}</span>
        </div>
        <div class="scroll-hint"><van-icon name="down" /> 向下滑动</div>
      </div>
      <div class="hero-glows">
        <div class="glow g1"></div>
        <div class="glow g2"></div>
        <div class="glow g3"></div>
      </div>
    </section>

    <!-- 纪念时光（累计相伴） -->
    <section class="section reveal">
      <h2 class="section-title">我们的时光</h2>
      <div class="counter">
        <div class="counter-cell">
          <div class="num">{{ timeData.days }}</div>
          <div class="lab">天</div>
        </div>
        <div class="counter-cell">
          <div class="num">{{ timeData.hours }}</div>
          <div class="lab">小时</div>
        </div>
        <div class="counter-cell">
          <div class="num">{{ timeData.minutes }}</div>
          <div class="lab">分钟</div>
        </div>
        <div class="counter-cell">
          <div class="num">{{ timeData.seconds }}</div>
          <div class="lab">秒</div>
        </div>
      </div>
      <p class="counter-note">自从那天起，时间被温柔地标注了你的名字</p>
    </section>

    <!-- 周年倒计时 -->
    <section class="section reveal">
      <h2 class="section-title">下一次纪念日倒计时</h2>
      <div class="counter">
        <div class="counter-cell">
          <div class="num">{{ countdown.days }}</div>
          <div class="lab">天</div>
        </div>
        <div class="counter-cell">
          <div class="num">{{ countdown.hours }}</div>
          <div class="lab">小时</div>
            </div>
        <div class="counter-cell">
          <div class="num">{{ countdown.minutes }}</div>
          <div class="lab">分钟</div>
          </div>
        <div class="counter-cell">
          <div class="num">{{ countdown.seconds }}</div>
          <div class="lab">秒</div>
        </div>
      </div>
      <p class="counter-note">从 {{ startDateText }} 出发的每一回眸，都值得被纪念</p>
    </section>

    <!-- 诗与短句 -->
    <section class="section reveal">
      <h2 class="section-title">写给你</h2>
      <div class="poems">
        <div class="poem-card" v-for="(line, i) in poemLines" :key="i">
          <p class="poem-text">{{ line }}</p>
        </div>
      </div>
    </section>

    <!-- 正文文案 -->
    <section class="section reveal">
      <h2 class="section-title">此刻的心声</h2>
      <div class="long-text">
        <p v-for="(p, i) in paragraphs" :key="i" class="long-p">{{ p }}</p>
      </div>
    </section>

    <!-- 图片画廊 -->
    <section class="section reveal">
      <h2 class="section-title">我们的照片</h2>
      <div class="gallery">
        <div class="img-card" v-for="(img, i) in images" :key="i">
          <div class="img-wrap">
            <img v-if="img.src" :src="img.src" :alt="img.alt || 'photo'" />
            <div v-else class="img-fallback" :style="{ background: img.fallback || 'linear-gradient(135deg,#ffd3e1,#ff8fb1)'}">
              <span>{{ img.alt || '与你相关的美好' }}</span>
            </div>
          </div>
          <div class="img-caption">{{ img.caption || '那天的风很轻，恰好吹过你' }}</div>
        </div>
      </div>
    </section>

    <!-- 音乐与视频 -->
    <section class="section media-section reveal">
      <h2 class="section-title">音乐和视频</h2>
      <div class="media-grid">
        <div class="media-card">
          <div class="media-title"><van-icon name="music-o" /> 我们的歌</div>
          <div class="media-body">
            <div class="audio-row">
              <button class="btn" @click="toggleMusic">{{ isPlaying ? '暂停' : '播放' }}</button>
              <span class="audio-state">{{ isPlaying ? '音乐播放中' : '点击播放音乐' }}</span>
            </div>
          </div>
        </div>
        <div class="media-card">
          <div class="media-title"><van-icon name="video-o" /> 我们的视频</div>
          <div class="media-body">
            <video class="video" controls :src="videoSrc" :poster="videoPoster"></video>
          </div>
        </div>
      </div>
    </section>

    <!-- 回忆卡片 -->
    <section class="section reveal">
      <h2 class="section-title">被珍藏的片刻</h2>
      <div class="memory-grid">
        <div class="memory-card" v-for="(m, i) in memories" :key="i" :style="{ '--bg': m.bg }">
          <div class="memory-inner">
            <h3 class="memory-title">{{ m.title }}</h3>
            <p class="memory-desc">{{ m.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 誓言 -->
    <section class="section reveal">
      <h2 class="section-title">我想对你说</h2>
      <ul class="vows">
        <li v-for="(v, i) in vows" :key="i" class="vow-item">
          <van-icon name="like-o" />
          <span>{{ v }}</span>
        </li>
      </ul>
    </section>

    <!-- 结语 -->
    <section class="section last reveal">
      <p class="ending">愿此后漫长岁月，我们把平凡过成心动，把日常过成喜欢。</p>
      <p class="signature">— 永远喜欢你</p>
    </section>

    <!-- 音乐 -->
    <audio ref="audio" :src="audioSrc" loop></audio>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const audio = ref(null)
const isPlaying = ref(false)
const audioSrc = ref('/src/views/love/Love_files/love.mp3')

// 页面标题与副标题
const pageTitle = ref('为你，写满整片星河')
const pageSubtitle = ref('当我遇见你，漫长人间忽然有了答案')

// 头像与姓名
const profile = ref({
  nameA: '阿黎',
  nameB: '阿念',
  avatarA: '', // 可替换为图片链接
  avatarB: ''  // 可替换为图片链接
})
const initials = (name) => (name || '?').slice(0, 1)
const avatarStyle = (url) => {
  if (url) {
    return { background: `center/cover no-repeat url(${url})` }
  }
  const colors = ['#ffd3e1','#a7c5ff','#bdf8e5','#f8dfff']
  const c = colors[Math.floor(Math.random()*colors.length)]
  return { background: c }
}

// 计时
const timeData = ref({ days: 0, hours: 0, minutes: 0, seconds: 0 })
const startDate = ref(new Date('2024-01-01T00:00:00'))
const startDateText = ref('2024-01-01')

const calculateTime = () => {
  const now = new Date()
  const diff = now.getTime() - startDate.value.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)
  timeData.value = { days, hours, minutes, seconds }
}

// 周年倒计时（下一次纪念日）
const countdown = ref({ days: 0, hours: 0, minutes: 0, seconds: 0 })
const calculateAnniversaryCountdown = () => {
  const base = startDate.value
  const now = new Date()
  const currentYear = now.getFullYear()
  let target = new Date(currentYear, base.getMonth(), base.getDate(), 0, 0, 0)
  if (target.getTime() <= now.getTime()) {
    target = new Date(currentYear + 1, base.getMonth(), base.getDate(), 0, 0, 0)
  }
  const diff = target.getTime() - now.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)
  countdown.value = { days, hours, minutes, seconds }
}

// 英雄区打字效果
const typedText = ref('')
const typingLines = [
  '喜欢清晨的风，也喜欢你温柔的眼睛',
  '想把碎碎念念写成诗，然后一字一句都给你',
  '把余生寄给你，连同每一个日出与晚安'
]
let typingTimer = null
let typingIndex = 0
let lineIndex = 0

const typeNext = () => {
  if (lineIndex >= typingLines.length) {
    lineIndex = 0
  }
  const current = typingLines[lineIndex]
  if (typingIndex <= current.length) {
    typedText.value = current.slice(0, typingIndex)
    typingIndex++
    typingTimer = setTimeout(typeNext, 80)
  } else {
    setTimeout(() => {
      typingIndex = 0
      lineIndex++
      typeNext()
    }, 1500)
  }
}

// 回忆卡片
const memories = ref([
  { title: '初见', desc: '从此山河有意，万物皆甜。', bg: 'linear-gradient(135deg,#ff8fb1,#ffd3e1)' },
  { title: '牵手', desc: '十指相扣，是我最笃定的信念。', bg: 'linear-gradient(135deg,#a7c5ff,#d4e4ff)' },
  { title: '惊喜', desc: '你说的每一句“在呢”，我都很安心。', bg: 'linear-gradient(135deg,#c3b7ff,#f8dfff)' },
  { title: '陪伴', desc: '答案在你眼睛里，光也在你眼睛里。', bg: 'linear-gradient(135deg,#bdf8e5,#e7fff6)' }
])

// 诗
const poemLines = ref([
  '你一颦一笑，落在我心上，开出漫山花。',
  '我绕过山海，走向你，原来你便是人间。',
  '愿你枕着晚风入梦，我在梦里等你。',
  '把心事藏进月亮里，等风也等你。'
])

// 誓言
const vows = ref([
  '在所有日子里，偏爱与你并肩的每一个普通瞬间',
  '把耐心、温柔与欢喜，都留给你',
  '不拿你和任何人比较，只把你放在心上',
  '把余生的答案，写成你的名字'
])

// 正文段落
const paragraphs = ref([
  '你来之前，我也在好好生活；你来了以后，我开始期待每一个明天。',
  '我们把日子过得很慢，慢到每一个眼神都能被认真收藏。',
  '愿往后的故事，字字不离“我们”。'
])

// 图片画廊（如有实际图片地址，可替换 src）
const images = ref([
  { src: '', alt: '第一次相遇', caption: '第一次相遇，风也有了甜味' },
  { src: '', alt: '第一次牵手', caption: '牵起你的手，世界就只剩你我' },
  { src: '', alt: '旅行', caption: '把山海踩在脚下，把你放在心上' },
  { src: '', alt: '日常', caption: '平常的一天，也被你点亮' }
])

// 视频
const videoSrc = ref('')
const videoPoster = ref('')

// 音乐控制
const toggleMusic = () => {
  if (!audio.value) return
  if (isPlaying.value) {
    audio.value.pause()
    isPlaying.value = false
  } else {
    audio.value.play().then(() => {
      isPlaying.value = true
    }).catch(() => showToast('自动播放受限制，请手动播放'))
  }
}

// 返回
const goBack = () => router.go(-1)

// 进入视口动画
let observer = null
const observeReveals = () => {
  observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add('show')
      }
    })
  }, { threshold: 0.15 })
  document.querySelectorAll('.reveal').forEach(el => observer.observe(el))
}

// 心形粒子
const heartCanvas = ref(null)
let ctx = null
let animationId = null
const hearts = []

function createHeart(width, height) {
  const size = Math.random() * 8 + 6
  return {
    x: Math.random() * width,
    y: height + Math.random() * 80,
    size,
    speed: Math.random() * 0.8 + 0.4,
    alpha: Math.random() * 0.6 + 0.3,
    swing: Math.random() * 1.2 + 0.2,
    phase: Math.random() * Math.PI * 2
  }
}

function drawHeart(x, y, size, color) {
  ctx.save()
  ctx.translate(x, y)
  ctx.scale(size / 16, size / 16)
  ctx.fillStyle = color
  ctx.beginPath()
  ctx.moveTo(0, 6)
  ctx.bezierCurveTo(0, -4, -8, -4, -8, 4)
  ctx.bezierCurveTo(-8, 10, 0, 14, 0, 16)
  ctx.bezierCurveTo(0, 14, 8, 10, 8, 4)
  ctx.bezierCurveTo(8, -4, 0, -4, 0, 6)
  ctx.closePath()
  ctx.fill()
  ctx.restore()
}

const renderHearts = () => {
  if (!ctx) return
  const canvas = heartCanvas.value
  const { width, height } = canvas
  ctx.clearRect(0, 0, width, height)

  if (hearts.length < 60) {
    hearts.push(createHeart(width, height))
  }

  for (let i = hearts.length - 1; i >= 0; i--) {
    const h = hearts[i]
    h.y -= h.speed
    h.x += Math.sin(h.phase += 0.02) * h.swing
    h.alpha -= 0.0015
    const color = `rgba(255, 176, 189, ${Math.max(h.alpha, 0)})`
    drawHeart(h.x, h.y, h.size, color)
    if (h.y < -20 || h.alpha <= 0) {
      hearts.splice(i, 1)
    }
  }
  animationId = requestAnimationFrame(renderHearts)
}

const resizeCanvas = () => {
  const canvas = heartCanvas.value
  if (!canvas) return
  const ratio = window.devicePixelRatio || 1
  canvas.width = Math.floor(canvas.clientWidth * ratio)
  canvas.height = Math.floor(canvas.clientHeight * ratio)
  if (ctx) ctx.scale(ratio, ratio)
}

onMounted(() => {
  // 时间
  calculateTime()
  const timer = setInterval(calculateTime, 1000)
  calculateAnniversaryCountdown()
  const timer2 = setInterval(calculateAnniversaryCountdown, 1000)

  // 打字机
  typeNext()

  // 观察入场
  observeReveals()

  // 音乐尝试自动播放
  setTimeout(() => {
    if (!audio.value) return
    audio.value.play().then(() => { isPlaying.value = true }).catch(() => {})
  }, 600)

  // 粒子
  const canvas = heartCanvas.value
  ctx = canvas.getContext('2d')
  resizeCanvas()
  renderHearts()
  window.addEventListener('resize', resizeCanvas)
  
  onUnmounted(() => {
    clearInterval(timer)
    clearInterval(timer2)
    if (animationId) cancelAnimationFrame(animationId)
    if (typingTimer) clearTimeout(typingTimer)
    window.removeEventListener('resize', resizeCanvas)
    if (observer) observer.disconnect()
    if (audio.value) audio.value.pause()
  })
})
</script>

<style scoped>
.love-page {
  min-height: 100vh;
  position: relative;
  background: radial-gradient(1200px 600px at 20% -10%, rgba(255, 182, 193, .28), transparent 60%),
              radial-gradient(1200px 600px at 80% 110%, rgba(173, 216, 230, .25), transparent 60%),
              linear-gradient(135deg, #1b1b3a, #3c2a4d 60%, #662e57);
  overflow-x: hidden;
  color: #fff;
}

.heart-canvas {
  position: fixed;
  inset: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.music-control,
.back-button {
  position: fixed;
  top: 16px;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(10px);
  padding: 10px 14px;
  border-radius: 999px;
  cursor: pointer;
  transition: transform .2s ease, background .2s ease;
}

.music-control:hover,
.back-button:hover {
  background: rgba(255, 255, 255, 0.16);
  transform: translateY(-2px);
}

.music-control { right: 16px; }
.back-button { left: 16px; }
.music-text { font-size: 12px; opacity: .9; }

/* 英雄区 */
.hero {
  position: relative;
  min-height: 84vh;
  display: grid;
  place-items: center;
  text-align: center;
  padding: 96px 20px 40px;
  z-index: 1;
}

.hero-inner { max-width: 920px; }

.profile { margin-bottom: 12px; }
.avatar-pair { display: flex; align-items: center; justify-content: center; gap: 12px; margin-bottom: 6px; }
.avatar {
  width: 56px; height: 56px; border-radius: 999px; display: grid; place-items: center;
  font-weight: 800; color: #2a1730; background: #ffd3e1; border: 2px solid rgba(255,255,255,.5);
  overflow: hidden;
}
.heart-mid { color: #ff8fb1; font-size: 18px; opacity: .9; }
.names { font-size: 13px; opacity: .9; }

.hero-title {
  font-size: 44px;
  line-height: 1.18;
  margin: 0 0 12px;
  font-weight: 800;
  letter-spacing: .5px;
  text-shadow: 0 10px 30px rgba(255, 182, 193, .18);
}

.hero-subtitle {
  font-size: 16px;
  opacity: .9;
  margin: 0 0 14px;
}

.hero-type {
  font-size: 14px;
  color: rgba(255,255,255,.9);
  min-height: 22px;
}

.type-cursor {
  position: relative;
}
.type-cursor::after {
  content: '';
  position: absolute;
  right: -6px;
  top: 0;
  width: 2px;
  height: 1.1em;
  background: rgba(255,255,255,.9);
  animation: blink 1s steps(1) infinite;
}

@keyframes blink { 50% { opacity: 0; } }

.scroll-hint {
  margin-top: 22px;
  font-size: 12px;
  opacity: .8;
}

.hero-glows { position: absolute; inset: 0; z-index: -1; overflow: hidden; }
.glow { position: absolute; filter: blur(60px); opacity: .4; }
.g1 { width: 380px; height: 380px; background: #ff8fb1; top: -60px; left: -60px; animation: float 10s ease-in-out infinite; }
.g2 { width: 460px; height: 460px; background: #a7c5ff; bottom: -80px; right: -40px; animation: float 12s ease-in-out infinite reverse; }
.g3 { width: 300px; height: 300px; background: #ffd3e1; top: 40%; left: 50%; transform: translate(-50%,-50%); animation: float 14s ease-in-out infinite; }
@keyframes float { 0%,100%{ transform: translateY(0) } 50%{ transform: translateY(-16px) } }

/* 通用段落 */
.section {
  position: relative;
  z-index: 1;
  padding: 56px 20px;
}
.section-title {
  text-align: center;
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 24px;
}

/* 入场动画 */
.reveal { opacity: 0; transform: translateY(40px); transition: .8s ease; }
.reveal.show { opacity: 1; transform: translateY(0); }

/* 计数器 */
.counter {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}
.counter-cell {
  min-width: 92px;
  background: rgba(255,255,255,.08);
  border: 1px solid rgba(255,255,255,.18);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 14px 18px;
  text-align: center;
}
.num { font-size: 32px; font-weight: 800; line-height: 1; }
.lab { font-size: 12px; opacity: .85; margin-top: 6px; }
.counter-note { text-align: center; opacity: .85; margin-top: 14px; font-size: 13px; }

/* 诗卡 */
.poems {
  display: grid;
  grid-template-columns: repeat(2, minmax(0,1fr));
  gap: 16px;
  max-width: 840px;
  margin: 0 auto;
}
.poem-card {
  background: rgba(255,255,255,.08);
  border: 1px solid rgba(255,255,255,.16);
  border-radius: 16px;
  padding: 18px;
  backdrop-filter: blur(8px);
  transition: transform .2s ease, background .2s ease;
}
.poem-card:hover {
  background: rgba(255,255,255,.12);
  transform: translateY(-4px);
}
.poem-text { margin: 0; line-height: 1.8; font-size: 14px; }

/* 正文 */
.long-text { max-width: 840px; margin: 0 auto; display: grid; gap: 12px; }
.long-p {
  margin: 0;
  background: rgba(255,255,255,.08);
  border: 1px solid rgba(255,255,255,.16);
  border-radius: 12px;
  padding: 14px 16px;
  line-height: 1.9;
  font-size: 14px;
}

/* 画廊 */
.gallery {
  max-width: 980px; margin: 0 auto; display: grid; gap: 16px;
  grid-template-columns: repeat(2, minmax(0,1fr));
}
.img-card { background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.12); border-radius: 14px; overflow: hidden; }
.img-wrap { aspect-ratio: 16/10; background: rgba(0,0,0,.18); position: relative; }
.img-wrap img { width: 100%; height: 100%; object-fit: cover; display: block; }
.img-fallback { width: 100%; height: 100%; display: grid; place-items: center; color: #2a1730; font-weight: 700; }
.img-caption { padding: 10px 12px; font-size: 12px; opacity: .85; }

/* 媒体 */
.media-section { padding-top: 12px; }
.media-grid { max-width: 980px; margin: 0 auto; display: grid; gap: 16px; grid-template-columns: 1fr 1fr; }
.media-card { background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.12); border-radius: 14px; overflow: hidden; }
.media-title { padding: 12px 14px; font-weight: 700; border-bottom: 1px solid rgba(255,255,255,.08); display: flex; align-items: center; gap: 8px; }
.media-body { padding: 12px 14px; }
.audio-row { display: flex; align-items: center; gap: 10px; }
.btn { padding: 8px 14px; border-radius: 999px; background: rgba(255,255,255,.85); color: #2a1730; border: none; cursor: pointer; font-weight: 700; }
.btn:hover { background: #fff; }
.video { width: 100%; max-height: 360px; background: #000; border-radius: 8px; }

/* 回忆卡片 */
.memory-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0,1fr));
  gap: 16px;
  max-width: 920px;
  margin: 0 auto;
}
.memory-card {
  position: relative;
  border-radius: 18px;
  overflow: hidden;
  background: var(--bg);
  box-shadow: 0 20px 60px rgba(0,0,0,.25);
  transform: translateY(0);
  transition: transform .3s ease, box-shadow .3s ease;
}
.memory-card:hover { transform: translateY(-6px); box-shadow: 0 30px 80px rgba(0,0,0,.32); }
.memory-inner { padding: 24px; text-align: center; color: #2a1730; }
.memory-title { margin: 0 0 8px; font-size: 18px; font-weight: 800; }
.memory-desc { margin: 0; opacity: .85; line-height: 1.7; }

/* 誓言 */
.vows {
  list-style: none;
  padding: 0;
  margin: 0 auto;
  max-width: 720px;
  display: grid;
  gap: 12px;
}
.vow-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255,255,255,.08);
  border: 1px solid rgba(255,255,255,.16);
  border-radius: 14px;
  padding: 12px 14px;
}
.vow-item span { font-size: 14px; }

/* 结语 */
.last { padding-bottom: 80px; }
.ending { text-align: center; opacity: .92; margin: 0 0 8px; }
.signature { text-align: center; opacity: .8; font-size: 13px; margin: 0; }

/* 响应式 */
@media (max-width: 768px) {
  .hero-title { font-size: 28px; }
  .poems, .memory-grid { grid-template-columns: 1fr; }
  .gallery { grid-template-columns: 1fr; }
  .media-grid { grid-template-columns: 1fr; }
}
</style>
