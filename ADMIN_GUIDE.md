# 后台管理使用指南 🔧

## 访问方式

### 方法一：直接访问
- 访问 `http://localhost:3000/admin/login`
- 使用默认账号登录

### 方法二：从首页进入
- 访问首页 `http://localhost:3000`
- 点击 "后台管理" 按钮
- 进入登录页面

## 登录信息

**默认账号：**
- 用户名：`admin`
- 密码：`admin`

## 功能模块

### 1. 首页 (`/admin`)
- 显示统计信息
- 快速操作入口
- 最近创建的日记

### 2. 创建日记 (`/admin/diary/create`)
- 添加新的日记记录
- 上传图片、音频、视频
- 设置标题和描述

### 3. 日记列表 (`/admin/diary/list`)
- 查看所有日记记录
- 编辑和删除功能
- 搜索和筛选

### 4. 编辑日记 (`/admin/diary/edit/:id`)
- 修改现有日记内容
- 更新媒体文件
- 保存修改

## 导航菜单

后台管理页面顶部有导航菜单，包含：
- 🏠 **首页** - 返回管理首页
- ➕ **创建日记** - 新建日记记录
- 📋 **日记列表** - 管理所有日记
- ⚙️ **退出登录** - 安全退出

## 安全说明

- 登录状态保存在 `localStorage` 中
- 未登录用户会被重定向到登录页面
- 退出登录会清除登录状态

## 技术实现

### 路由配置
```javascript
{
  path: '/admin/login',
  name: 'AdminLogin',
  component: () => import('@/views/admin/Login.vue')
},
{
  path: '/admin',
  name: 'Admin',
  component: () => import('@/views/admin/AdminLayout.vue'),
  meta: { requiresAuth: true },
  children: [
    // 子路由配置
  ]
}
```

### 路由守卫
- 检查 `requiresAuth` 元信息
- 验证 `localStorage` 中的 token
- 未登录时重定向到登录页面

## 开发说明

当前版本为演示版本，包含：
- ✅ 完整的登录流程
- ✅ 基础的后台界面
- ✅ 路由权限控制
- ✅ 响应式设计

后续可以扩展：
- 🔄 真实的后端API集成
- 🔐 更安全的身份验证
- 📊 数据统计功能
- 🎨 更多自定义选项

---

**注意：** 这是一个演示版本，实际部署时请修改默认密码并添加更多安全措施。 