// 测试新的倒计时格式（天时分秒）
const dayjs = require('dayjs')

// 模拟当前日期和时间
const now = dayjs('2024-12-20 14:30:25') // 假设现在是2024年12月20日14:30:25
console.log('当前时间:', now.format('YYYY-MM-DD HH:mm:ss'))

// 测试数据：不同的时间差
const testScenarios = [
  {
    name: '未来5天',
    targetDate: '2024-12-25 10:00:00',
    expected: '5天'
  },
  {
    name: '未来2小时',
    targetDate: '2024-12-20 16:30:25',
    expected: '2时'
  },
  {
    name: '未来30分钟',
    targetDate: '2024-12-20 15:00:25',
    expected: '30分'
  },
  {
    name: '未来10秒',
    targetDate: '2024-12-20 14:30:35',
    expected: '10秒'
  },
  {
    name: '未来1天2小时30分15秒',
    targetDate: '2024-12-21 17:00:40',
    expected: '1天2时30分15秒'
  }
]

console.log('\n=== 倒计时格式测试 ===')

testScenarios.forEach(scenario => {
  const target = dayjs(scenario.targetDate)
  const diff = target.diff(now)
  
  if (diff <= 0) {
    console.log(`${scenario.name}: 就是现在！`)
  } else {
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((diff % (1000 * 60)) / 1000)
    
    let countdown = ''
    if (days > 0) {
      countdown = `${days}天${hours}时${minutes}分${seconds}秒`
    } else if (hours > 0) {
      countdown = `${hours}时${minutes}分${seconds}秒`
    } else if (minutes > 0) {
      countdown = `${minutes}分${seconds}秒`
    } else {
      countdown = `${seconds}秒`
    }
    
    console.log(`${scenario.name}: ${countdown}`)
  }
})

// 测试纪念日倒计时逻辑
console.log('\n=== 纪念日倒计时测试 ===')

const anniversaryDates = [
  { name: '生日', date: '2024-12-25' },
  { name: '新年', date: '2025-01-01' },
  { name: '情人节', date: '2025-02-14' }
]

anniversaryDates.forEach(anniversary => {
  const anniversaryDate = dayjs(anniversary.date)
  
  // 计算到今年纪念日的时间
  let targetDate = anniversaryDate.year(now.year())
  if (targetDate.isBefore(now)) {
    targetDate = anniversaryDate.year(now.year() + 1)
  }
  
  const diff = targetDate.diff(now)
  
  if (diff <= 0) {
    console.log(`${anniversary.name}: 就是今天！🎉`)
  } else {
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((diff % (1000 * 60)) / 1000)
    
    let countdown = ''
    if (days > 0) {
      countdown = `${days}天${hours}时${minutes}分${seconds}秒`
    } else if (hours > 0) {
      countdown = `${hours}时${minutes}分${seconds}秒`
    } else if (minutes > 0) {
      countdown = `${minutes}分${seconds}秒`
    } else {
      countdown = `${seconds}秒`
    }
    
    console.log(`${anniversary.name}: ${countdown}`)
  }
})

console.log('\n✅ 倒计时格式测试完成！')
console.log('📝 格式说明:')
console.log('  - 有天数时: X天X时X分X秒')
console.log('  - 无天数时: X时X分X秒')
console.log('  - 无小时时: X分X秒')
console.log('  - 无分钟时: X秒')
console.log('  - 已到时: 就是今天！🎉')

