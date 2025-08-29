<template>
  <div class="love-tree-page">
    <div id="wrap">
      <!-- 添加一些可见的调试元素 -->
      <div class="debug-info">
        <p>Canvas状态: {{ canvasStatus }}</p>
        <p>种子位置: {{ seedPosition }}</p>
        <p>点击次数: {{ clickCount }}</p>
      </div>
      
      <div id="text">
        <div id="code">
          <span class="say">哈喽，真真</span><br>
          <span class="say">认识你很开心</span><br>
          <span class="say">自从认识你之后，生活充满了期待</span><br>
          <span class="say">想和你见面，跟你拥抱，跟你腻歪，尤其是面对面拥抱，真的很治愈</span><br>
          <span class="say">你是疲惫生活里的唯一解药</span><br>
          <span class="say">遇见你很晚，但我会陪你很久</span><br>
          <span class="say">你若一直在，我便一直爱</span><br>
          <span class="say">习惯了每天都能和你说说话</span><br>
          <span class="say">习惯了每天都能和你聊聊天</span><br>
          <span class="say">习惯了每天都能和你说早安</span><br>
          <span class="say">我会每一晚与你道声晚安再入梦乡</span><br>
          <span class="say">我会带你去所有你想去的地方</span><br>
          <span class="say">陪你闹看你笑</span><br>
          <span class="say">历经你生命中所有的点点滴滴</span><br>
          <span class="say">如果我有什么让你不开心了</span><br>
          <span class="say">你一定要及时告诉我</span><br>
          <span class="say">不许在心里偷偷减我分</span><br>
          <span class="say">更不许你独自难过</span><br>
          <br>
          <span class="say">你很可爱，不是cute</span><br>
          <span class="say">而是 could be loved</span><br>
          <span class="say">我对你的爱只增不减</span><br>
          <span class="say">不见面的日子里</span><br>
          <span class="say">我依然爱你♥</span><br>
          <br>
          <span class="say"><span class="space"></span> -- Yours,齐齐.</span>
        </div>
      </div>
      
      <div id="clock-box">
        <a href="#" target="_blank">@真真</a> 与 <a href="#" target="_blank">@齐齐</a> 相遇的
        <div id="clock">{{ timeElapsed }}</div>
      </div>
      
      <canvas 
        ref="canvasRef" 
        id="canvas" 
        width="1100" 
        height="680"
        @click="handleCanvasClick"
        @mousemove="handleCanvasMouseMove"
        :class="{ hand: isHovering }"
      ></canvas>
    </div>
    
    <audio ref="audioRef" src="/love.mp3" autoplay></audio>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'

export default {
  name: 'LoveTree',
  setup() {
    const canvasRef = ref(null)
    const audioRef = ref(null)
    const isHovering = ref(false)
    const timeElapsed = ref('')
    const canvasStatus = ref('未初始化')
    const seedPosition = ref('未知')
    const clickCount = ref(0)
    
    let tree = null
    let seed = null
    let foot = null
    let hold = 1
    let animationId = null
    let together = null
    let timer = null

    // 樱花树配置
    const opts = {
      seed: {
        x: 550,
        color: "rgb(190, 26, 37)",
        scale: 2
      },
      branch: [
        [535, 680, 570, 250, 500, 200, 30, 100, [
          [540, 500, 455, 417, 340, 400, 13, 100, [
            [450, 435, 434, 430, 394, 395, 2, 40]
          ]],
          [550, 445, 600, 356, 680, 345, 12, 100, [
            [578, 400, 648, 409, 661, 426, 3, 80]
          ]],
          [539, 281, 537, 248, 534, 217, 3, 40],
          [546, 397, 413, 247, 328, 244, 9, 80, [
            [427, 286, 383, 253, 371, 205, 2, 40],
            [498, 345, 435, 315, 395, 330, 4, 60]
          ]],
          [546, 357, 608, 252, 678, 221, 6, 100, [
            [590, 293, 646, 277, 648, 271, 2, 80]
          ]]
        ]]
      ],
      bloom: {
        num: 700,
        width: 1080,
        height: 650,
      },
      footer: {
        width: 1200,
        height: 5,
        speed: 10,
      }
    }

    // 时间计算函数
    const timeElapse = (date) => {
      const current = new Date()
      let seconds = (current - date) / 1000
      const days = Math.floor(seconds / (3600 * 24))
      seconds = seconds % (3600 * 24)
      const hours = Math.floor(seconds / 3600)
      if (seconds < 3600) {
        const minutes = Math.floor(seconds / 60)
        seconds = Math.floor(seconds % 60)
        timeElapsed.value = `${days}天${hours}小时${minutes}分钟${seconds}秒`
      } else {
        timeElapsed.value = `${days}天${hours}小时`
      }
    }

    // 打字机效果
    const typewriter = (element) => {
      const text = element.textContent
      element.textContent = ''
      let i = 0
      
      const type = () => {
        if (i < text.length) {
          element.textContent += text.charAt(i)
          i++
          setTimeout(type, 100)
        }
      }
      
      type()
    }

    // 樱花树类
    class Tree {
      constructor(canvas, width, height, opts) {
        this.canvas = canvas
        this.ctx = canvas.getContext('2d')
        this.width = width
        this.height = height
        this.opts = opts
        
        this.seed = new Seed(this)
        this.branches = []
        this.blooms = []
        this.footer = new Footer(this)
        
        this.init()
      }

      init() {
        this.initBranches()
        this.initBlooms()
      }

      initBranches() {
        const branch = this.opts.branch
        for (let i = 0; i < branch.length; i++) {
          this.branches.push(new Branch(this, branch[i]))
        }
      }

      initBlooms() {
        for (let i = 0; i < this.opts.bloom.num; i++) {
          this.blooms.push(new Bloom(this))
        }
      }

      draw() {
        this.ctx.clearRect(0, 0, this.width, this.height)
        
        this.branches.forEach(branch => branch.draw())
        this.blooms.forEach(bloom => bloom.draw())
        this.footer.draw()
      }

      grow() {
        this.branches.forEach(branch => branch.grow())
      }

      canGrow() {
        return this.branches.some(branch => branch.canGrow())
      }

      flower() {
        this.blooms.forEach(bloom => bloom.flower())
      }

      canFlower() {
        return this.blooms.some(bloom => bloom.canFlower())
      }

      snapshot(id, x, y, width, height) {
        const canvas = document.createElement('canvas')
        canvas.width = width
        canvas.height = height
        const ctx = canvas.getContext('2d')
        ctx.drawImage(this.canvas, x, y, width, height, 0, 0, width, height)
        this.snapshots = this.snapshots || {}
        this.snapshots[id] = canvas
      }

      move(id, x, y) {
        if (this.snapshots && this.snapshots[id]) {
          this.ctx.clearRect(0, 0, this.width, this.height)
          this.ctx.drawImage(this.snapshots[id], x, y)
          return true
        }
        return false
      }

      jump() {
        this.blooms.forEach(bloom => bloom.jump())
      }

      toDataURL(type) {
        return this.canvas.toDataURL(type)
      }
    }

    // 种子类
    class Seed {
      constructor(tree) {
        this.tree = tree
        this.x = tree.opts.seed.x
        this.y = tree.height
        this.color = tree.opts.seed.color
        this.scale = tree.opts.seed.scale
        this.opacity = 1
      }

      draw() {
        const ctx = this.tree.ctx
        ctx.save()
        ctx.globalAlpha = this.opacity
        ctx.fillStyle = this.color
        ctx.beginPath()
        ctx.arc(this.x, this.y, this.scale, 0, 2 * Math.PI)
        ctx.fill()
        ctx.restore()
      }

      hover(x, y) {
        const distance = Math.sqrt((x - this.x) ** 2 + (y - this.y) ** 2)
        return distance < 20
      }

      canScale() {
        return this.scale > 0.5
      }

      scale(value) {
        this.scale *= value
      }

      canMove() {
        return this.y > 0
      }

      move(x, y) {
        this.x += x
        this.y += y
      }
    }

    // 树枝类
    class Branch {
      constructor(tree, data) {
        this.tree = tree
        this.data = data
        this.x = data[0]
        this.y = data[1]
        this.endX = data[2]
        this.endY = data[3]
        this.length = data[4]
        this.angle = data[5]
        this.width = data[6]
        this.opacity = data[7]
        this.children = data[8] || []
        
        this.grown = 0
        this.growing = true
      }

      draw() {
        if (!this.growing) return
        
        const ctx = this.tree.ctx
        ctx.save()
        ctx.globalAlpha = this.opacity
        ctx.strokeStyle = '#8B4513'
        ctx.lineWidth = this.width
        ctx.lineCap = 'round'
        
        ctx.beginPath()
        ctx.moveTo(this.x, this.y)
        ctx.lineTo(this.endX, this.endY)
        ctx.stroke()
        ctx.restore()
      }

      grow() {
        if (!this.growing) return
        
        this.grown += 0.1
        if (this.grown >= 1) {
          this.growing = false
          this.children.forEach(child => {
            this.tree.branches.push(new Branch(this.tree, child))
          })
        }
      }

      canGrow() {
        return this.growing
      }
    }

    // 花朵类
    class Bloom {
      constructor(tree) {
        this.tree = tree
        this.x = Math.random() * tree.opts.bloom.width
        this.y = Math.random() * tree.opts.bloom.height
        this.size = Math.random() * 3 + 1
        this.opacity = Math.random() * 0.5 + 0.5
        this.color = `hsl(${Math.random() * 60 + 300}, 70%, 50%)`
        this.falling = false
        this.fallSpeed = Math.random() * 2 + 1
      }

      draw() {
        const ctx = this.tree.ctx
        ctx.save()
        ctx.globalAlpha = this.opacity
        ctx.fillStyle = this.color
        ctx.beginPath()
        ctx.arc(this.x, this.y, this.size, 0, 2 * Math.PI)
        ctx.fill()
        ctx.restore()
      }

      flower() {
        if (this.falling) {
          this.y += this.fallSpeed
          if (this.y > this.tree.height) {
            this.y = -10
            this.x = Math.random() * this.tree.opts.bloom.width
          }
        }
      }

      canFlower() {
        return true
      }

      jump() {
        this.falling = true
      }
    }

    // 底部类
    class Footer {
      constructor(tree) {
        this.tree = tree
        this.width = tree.opts.footer.width
        this.height = tree.opts.footer.height
        this.speed = tree.opts.footer.speed
        this.x = 0
      }

      draw() {
        const ctx = this.tree.ctx
        ctx.fillStyle = '#8B4513'
        ctx.fillRect(this.x, this.tree.height - this.height, this.width, this.height)
        
        this.x += this.speed
        if (this.x > this.tree.width) {
          this.x = -this.width
        }
      }
    }

    // 动画函数
    const animate = async () => {
      console.log('Starting animation')
      
      // 种子动画
      seed.draw()
      while (hold) {
        await new Promise(resolve => setTimeout(resolve, 10))
      }
      
      while (seed.canScale()) {
        seed.scale(0.95)
        await new Promise(resolve => setTimeout(resolve, 10))
      }
      
      while (seed.canMove()) {
        seed.move(0, 2)
        foot.draw()
        await new Promise(resolve => setTimeout(resolve, 10))
      }

      // 生长动画
      do {
        tree.grow()
        await new Promise(resolve => setTimeout(resolve, 10))
      } while (tree.canGrow())

      // 开花动画
      do {
        tree.flower()
        await new Promise(resolve => setTimeout(resolve, 10))
      } while (tree.canFlower())

      // 移动动画
      tree.snapshot("p1", 240, 0, 610, 680)
      while (tree.move("p1", 500, 0)) {
        foot.draw()
        await new Promise(resolve => setTimeout(resolve, 10))
      }
      foot.draw()
      tree.snapshot("p2", 500, 0, 610, 680)

      // 文字动画
      const codeElement = document.getElementById('code')
      const clockBox = document.getElementById('clock-box')
      
      if (codeElement) {
        codeElement.style.display = 'block'
        typewriter(codeElement)
      }
      
      if (clockBox) {
        clockBox.style.opacity = '1'
      }

      // 跳跃动画
      while (true) {
        tree.ctx.clearRect(0, 0, tree.width, tree.height)
        tree.jump()
        foot.draw()
        await new Promise(resolve => setTimeout(resolve, 25))
      }
    }

    // 事件处理
    const handleCanvasClick = (e) => {
      const rect = canvasRef.value.getBoundingClientRect()
      const x = e.clientX - rect.left
      const y = e.clientY - rect.top
      
      clickCount.value++
      console.log('Canvas clicked at:', x, y)
      
      // 尝试播放音频
      if (audioRef.value) {
        audioRef.value.play().catch(error => {
          console.log('音频播放失败，可能是文件不存在或浏览器策略限制:', error)
        })
      }
      
      if (seed && seed.hover(x, y)) {
        console.log('Seed clicked!')
        hold = 0
        isHovering.value = false
        animate()
      }
    }

    const handleCanvasMouseMove = (e) => {
      const rect = canvasRef.value.getBoundingClientRect()
      const x = e.clientX - rect.left
      const y = e.clientY - rect.top
      
      if (seed) {
        isHovering.value = seed.hover(x, y)
      }
    }

    // 初始化
    const init = async () => {
      await nextTick()
      
      const canvas = canvasRef.value
      if (!canvas || !canvas.getContext) {
        canvasStatus.value = 'Canvas不可用'
        console.log('Canvas not available')
        return
      }

      const width = canvas.width
      const height = canvas.height

      canvasStatus.value = `Canvas已初始化: ${width}x${height}`
      console.log('Initializing tree with dimensions:', width, height)

      // 创建树实例
      tree = new Tree(canvas, width, height, opts)
      seed = tree.seed
      foot = tree.footer

      // 立即绘制种子
      seed.draw()
      seedPosition.value = `(${seed.x}, ${seed.y})`
      console.log('Seed drawn at:', seed.x, seed.y)

      // 设置时间
      together = new Date()
      together.setFullYear(2025, 4, 30)
      together.setHours(20)
      together.setMinutes(20)
      together.setSeconds(0)
      together.setMilliseconds(0)

      // 开始倒计时
      timer = setInterval(() => {
        timeElapse(together)
      }, 1000)

      console.log('Initialization complete')
    }

    // 生命周期
    onMounted(() => {
      init()
    })

    onUnmounted(() => {
      if (timer) {
        clearInterval(timer)
      }
      if (animationId) {
        cancelAnimationFrame(animationId)
      }
    })

    return {
      canvasRef,
      audioRef,
      isHovering,
      timeElapsed,
      canvasStatus,
      seedPosition,
      clickCount,
      handleCanvasClick,
      handleCanvasMouseMove
    }
  }
}
</script>

<style scoped>
.love-tree-page {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: #ffe;
}

#wrap {
  position: relative;
  width: 100%;
  height: 100%;
}

.debug-info {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(255, 255, 255, 0.9);
  padding: 10px;
  border-radius: 5px;
  z-index: 1000;
  font-size: 12px;
  color: #333;
}

.debug-info p {
  margin: 5px 0;
}

#text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
  text-align: center;
  color: #333;
  font-size: 16px;
  line-height: 1.8;
  pointer-events: none;
}

#code {
  display: none;
  font-family: 'Courier New', monospace;
}

.say {
  opacity: 0;
  animation: fadeIn 0.5s ease-in forwards;
}

@keyframes fadeIn {
  to {
    opacity: 1;
  }
}

#clock-box {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  text-align: center;
  color: #333;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.5s ease;
}

#clock {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b9d;
  margin-top: 5px;
}

#canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  cursor: default;
  transition: cursor 0.3s ease;
  border: 1px solid #ccc;
}

#canvas.hand {
  cursor: pointer;
}

a {
  color: #ff6b9d;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  #text {
    font-size: 14px;
    padding: 0 20px;
  }
  
  #clock-box {
    font-size: 12px;
  }
  
  #clock {
    font-size: 16px;
  }
}
</style>
