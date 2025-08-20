# 记录和女朋友的每一天 - 前端

## 项目介绍

这是一个浪漫的移动端应用，用于记录与女朋友的每一个美好瞬间。采用Vue3 + Vant4技术栈，具有现代化的UI设计和丰富的动画效果。

## 功能特性

- 📱 移动端友好的响应式设计
- 📅 时光日历，标记有回忆的日期
- 🖼️ 支持图片、视频、音频展示
- 📝 文字描述和标题
- 🎵 背景音乐播放
- ✨ 丰富的动画效果
- 💕 浪漫的视觉设计

## 技术栈

- **框架**: Vue 3 + Composition API
- **UI组件库**: Vant 4
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **构建工具**: Vite
- **样式**: SCSS
- **日期处理**: Day.js

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 项目结构

```
src/
├── components/          # 公共组件
├── views/              # 页面组件
│   ├── Home.vue        # 首页
│   ├── Calendar.vue    # 日历页面
│   ├── Detail.vue      # 详情页面
│   └── admin/          # 后台管理页面
├── router/             # 路由配置
├── stores/             # 状态管理
├── styles/             # 样式文件
│   ├── variables.scss  # 变量定义
│   └── index.scss      # 全局样式
└── main.js             # 入口文件
```

## 页面说明

### 首页 (Home.vue)
- 展示最新的回忆
- 图片轮播展示
- 背景音乐播放
- 跳转到日历页面

### 日历页面 (Calendar.vue)
- 月份切换
- 日期标记（有回忆的日期显示爱心）
- 回忆列表展示
- 点击日期查看详情

### 详情页面 (Detail.vue)
- 完整的回忆内容展示
- 图片、视频、音频播放
- 分享功能
- 返回导航

### 后台管理
- 回忆创建、编辑、删除
- 文件上传（图片、视频、音频）
- 数据管理

## 样式指南

### 颜色主题
- 主色调: `#ff6b9d` (粉色)
- 辅助色: `#ff8fab` (浅粉色)
- 背景渐变: `linear-gradient(135deg, #667eea 0%, #764ba2 100%)`

### 动画效果
- 心跳动画: 用于爱心图标
- 飘落花瓣: 背景装饰
- 滑动过渡: 页面切换
- 悬停效果: 交互反馈

## 开发说明

### 环境要求
- Node.js >= 16
- npm >= 8

### 开发规范
- 使用Composition API
- 组件命名采用PascalCase
- 文件命名采用kebab-case
- 样式使用SCSS，遵循BEM命名规范

### 代码检查
```bash
npm run lint
```

## 部署说明

### 构建
```bash
npm run build
```

### 部署到服务器
将`dist`目录下的文件部署到Web服务器即可。

### 环境变量
- `VITE_API_BASE_URL`: API基础地址
- `VITE_OSS_BASE_URL`: 阿里云OSS基础地址

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 许可证

MIT License 