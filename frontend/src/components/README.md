# BackButton 组件

一个可复用的返回按钮组件，具有统一的样式和交互效果。

## 使用方法

### 1. 导入组件

```javascript
import BackButton from '@/components/BackButton.vue'
```

### 2. 在模板中使用

```vue
<template>
  <div class="your-page">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 其他内容 -->
  </div>
</template>
```

## 组件特性

- ✅ **固定定位** - 按钮固定在左上角 (top: 20px, left: 20px)
- ✅ **毛玻璃效果** - 半透明背景 + 模糊效果
- ✅ **悬停动画** - 鼠标悬停时放大和背景变亮
- ✅ **高层级** - z-index: 1000 确保在最上层
- ✅ **智能返回** - 使用 router.back() 返回上一页

## 样式特点

- 40px × 40px 圆形按钮
- 半透明白色背景 (rgba(255, 255, 255, 0.2))
- 毛玻璃效果 (backdrop-filter: blur(10px))
- 白色边框 (rgba(255, 255, 255, 0.3))
- 悬停时背景变亮并放大 1.1 倍

## 已使用的页面

- ✅ Detail.vue - 日记详情页面
- ✅ Calendar.vue - 日历页面
- ✅ EditLetter.vue - 编辑信件页面
- ✅ Home.vue - 首页
- ✅ DiaryList.vue - 日记列表页面
- ✅ LetterBox.vue - 信箱页面

## 注意事项

1. 组件会自动处理返回逻辑，无需额外配置
2. 样式已经优化，适合各种背景色
3. 如果需要自定义返回逻辑，可以修改组件内部的 `goBack` 方法
