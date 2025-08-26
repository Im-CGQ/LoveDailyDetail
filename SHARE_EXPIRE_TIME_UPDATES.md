# 分享过期时间功能更新

## 更新内容

### 1. LetterDetail.vue 分享功能增强

#### 修改内容
- 添加了 `getShareExpireMinutes` 导入
- 修改了 `createShare` 方法，使其显示配置的分享过期时间
- 替换了硬编码的"有效期3小时"提示

#### 具体变更
```javascript
// 添加导入
import { getShareExpireMinutes } from '@/api/systemConfig'

// 修改分享方法
const createShare = async () => {
  // ... 创建分享链接逻辑
  
  // 获取分享过期时间配置并显示
  const minutes = await getShareExpireMinutes()
  const hours = Math.floor(minutes / 60)
  const remainingMinutes = minutes % 60
  let timeText = ''
  
  if (hours > 0) {
    timeText = `${hours}小时`
    if (remainingMinutes > 0) {
      timeText += `${remainingMinutes}分钟`
    }
  } else {
    timeText = `${remainingMinutes}分钟`
  }
  
  // 显示动态过期时间
  showToast(`分享链接已复制到剪贴板，将在${timeText}后过期`)
}
```

### 2. SystemConfigList.vue 分享时间配置优化

#### 修改内容
- 将分享过期时间选择器改为数字输入框
- 删除了预设的过期时间选项
- 添加了输入验证逻辑

#### 具体变更

**模板部分**:
```vue
<!-- 替换选择器为数字输入框 -->
<van-cell title="分享链接过期时间">
  <template #right-icon>
    <van-field
      v-model="shareExpireMinutes"
      type="number"
      placeholder="请输入分钟数"
      :border="false"
      style="width: 120px; text-align: right;"
      @blur="onExpireMinutesChange"
    />
    <span style="margin-left: 5px; color: #969799;">分钟</span>
  </template>
</van-cell>
```

**脚本部分**:
```javascript
// 删除不再需要的变量
// const showExpirePicker = ref(false)
// const expireOptions = [...]

// 新的输入处理方法
const onExpireMinutesChange = async () => {
  try {
    const minutes = parseInt(shareExpireMinutes.value)
    
    // 验证输入值
    if (isNaN(minutes) || minutes <= 0) {
      showToast('请输入有效的分钟数（大于0）')
      return
    }
    
    if (minutes > 10080) { // 7天 = 7 * 24 * 60 = 10080分钟
      showToast('过期时间不能超过7天')
      return
    }
    
    await setShareExpireMinutes(minutes)
    showToast('设置成功')
  } catch (error) {
    showToast(error.message)
  }
}
```

## 功能特点

### 1. 统一的过期时间显示
- `Detail.vue` 和 `LetterDetail.vue` 都使用相同的过期时间显示逻辑
- 自动将分钟数转换为小时和分钟的友好显示格式
- 支持纯分钟数显示（小于1小时时）

### 2. 灵活的时间配置
- 管理员可以输入任意分钟数（1-10080分钟，即7天）
- 实时验证输入值的有效性
- 支持精确到分钟的时间设置

### 3. 用户体验优化
- 数字输入框比选择器更直观
- 输入验证提供即时反馈
- 保持与系统其他配置的一致性

## 验证规则

1. **最小值**: 必须大于0分钟
2. **最大值**: 不能超过10080分钟（7天）
3. **格式**: 必须是有效的整数
4. **实时验证**: 在输入框失去焦点时进行验证

## 使用流程

1. **管理员配置**:
   - 进入系统配置页面
   - 在分享设置中输入期望的过期时间（分钟）
   - 系统自动验证并保存配置

2. **用户分享**:
   - 在日记详情页或信件详情页点击分享按钮
   - 系统显示配置的过期时间信息
   - 分享链接使用配置的过期时间

## 技术实现

- 使用 `van-field` 组件的 `type="number"` 属性确保输入数字
- 通过 `@blur` 事件在用户完成输入时触发验证
- 使用 `parseInt()` 转换字符串为数字进行验证
- 保持与现有系统配置框架的一致性
