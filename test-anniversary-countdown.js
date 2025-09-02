// 测试纪念日倒计时逻辑
const dayjs = require('dayjs')

// 模拟当前日期（可以修改来测试不同时间点）
const now = dayjs('2024-12-20') // 假设今天是2024年12月20日
console.log('当前日期:', now.format('YYYY-MM-DD'))

// 测试数据：包含过去和未来的纪念日
const anniversaryDates = [
  { name: '第一次见面', date: '2024-01-01' },      // 已过
  { name: '在一起纪念日', date: '2024-02-14' },    // 已过
  { name: '生日', date: '2024-12-25' },            // 未来5天
  { name: '新年', date: '2025-01-01' },            // 未来12天
  { name: '情人节', date: '2025-02-14' }           // 未来56天
]

console.log('\n纪念日列表:')
anniversaryDates.forEach(anniversary => {
  const daysDiff = dayjs(anniversary.date).diff(now, 'day')
  const status = daysDiff < 0 ? '已过' : daysDiff === 0 ? '今天' : `还有${daysDiff}天`
  console.log(`${anniversary.name}: ${anniversary.date} - ${status}`)
})

// 测试倒计时计算逻辑
console.log('\n=== 倒计时计算测试 ===')

let nextAnniversary = null
let minDays = Infinity

// 找到最近的纪念日
anniversaryDates.forEach(anniversary => {
  const anniversaryDate = dayjs(anniversary.date)
  
  // 计算到今年纪念日的天数
  let daysDiff = anniversaryDate.year(now.year()).diff(now, 'day')
  
  // 如果今年的纪念日已经过了，计算明年的
  if (daysDiff < 0) {
    daysDiff = anniversaryDate.year(now.year() + 1).diff(now, 'day')
  }
  
  console.log(`${anniversary.name}: 距离${daysDiff}天`)
  
  // 如果天数更少，更新为最近的纪念日
  if (daysDiff < minDays) {
    minDays = daysDiff
    nextAnniversary = anniversary
  }
})

console.log(`\n最近的纪念日: ${nextAnniversary.name} (${nextAnniversary.date})`)
console.log(`倒计时: ${minDays === 0 ? '就是今天！🎉' : `还有 ${minDays} 天`}`)

// 测试不同日期的倒计时
console.log('\n=== 不同日期的倒计时测试 ===')
const testDates = [
  '2024-12-20', // 今天
  '2024-12-21', // 明天
  '2024-12-25', // 生日
  '2025-01-01', // 新年
  '2025-02-14'  // 情人节
]

testDates.forEach(testDate => {
  const testNow = dayjs(testDate)
  console.log(`\n当前日期: ${testNow.format('YYYY-MM-DD')}`)
  
  let nextAnniversary = null
  let minDays = Infinity
  
  anniversaryDates.forEach(anniversary => {
    const anniversaryDate = dayjs(anniversary.date)
    let daysDiff = anniversaryDate.year(testNow.year()).diff(testNow, 'day')
    
    if (daysDiff < 0) {
      daysDiff = anniversaryDate.year(testNow.year() + 1).diff(testNow, 'day')
    }
    
    if (daysDiff < minDays) {
      minDays = daysDiff
      nextAnniversary = anniversary
    }
  })
  
  if (nextAnniversary) {
    const countdown = minDays === 0 ? '就是今天！🎉' : `还有 ${minDays} 天`
    console.log(`最近纪念日: ${nextAnniversary.name} - ${countdown}`)
  }
})

