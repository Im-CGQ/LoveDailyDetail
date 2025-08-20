# 登录系统说明

## 功能特性

### 1. 统一登录页面
- 前后台共用同一个登录页面 (`/login`)
- 支持前台用户和后台管理员两种角色
- 可通过URL参数 `?mode=admin` 或 `?mode=user` 切换登录模式

### 2. 登录状态持久化
- **记住我一个月**：勾选后登录状态保持30天
- **临时登录**：未勾选时登录状态保持1天
- 自动检查token过期时间，过期后自动清除状态

### 3. JWT Token管理
- 模拟JWT token生成和验证
- Token包含用户信息、角色、过期时间
- 自动处理token过期和刷新

### 4. 路由守卫
- 自动检查需要登录的页面
- 未登录时自动跳转到登录页面
- 登录成功后自动跳转到原目标页面

## 文件结构

```
frontend/src/
├── views/
│   ├── Login.vue              # 统一登录页面
│   └── admin/
│       └── AdminLayout.vue    # 后台布局（已更新）
├── utils/
│   └── auth.js               # 认证工具函数
├── api/
│   └── auth.js               # 认证API接口
└── router/
    └── index.js              # 路由配置（已更新）
```

## 使用方法

### 1. 前台登录
```
http://localhost:3000/login?mode=user
```

### 2. 后台登录
```
http://localhost:3000/login?mode=admin
```

### 3. 默认账号
- 用户名：`admin`
- 密码：`admin`

## 技术实现

### 1. 认证工具函数 (`utils/auth.js`)
- `generateToken()`: 生成JWT token
- `validateToken()`: 验证token有效性
- `saveLoginState()`: 保存登录状态
- `checkLoginState()`: 检查登录状态
- `clearLoginState()`: 清除登录状态
- `getCurrentUser()`: 获取当前用户信息
- `hasPermission()`: 检查权限

### 2. API接口 (`api/auth.js`)
- `login()`: 登录接口
- `logout()`: 登出接口
- `getUserInfo()`: 获取用户信息
- `refreshToken()`: 刷新token
- 自动处理token过期和401错误

### 3. 路由守卫
- 检查 `meta.requiresAuth` 字段
- 自动跳转到登录页面
- 支持重定向到原目标页面

### 4. 登录页面特性
- 响应式设计，支持移动端
- 浪漫的UI设计，符合应用主题
- 支持记住登录状态
- 模式切换功能
- 加载状态和错误提示

## 状态管理

### localStorage 存储项
- `auth_token`: JWT token
- `auth_role`: 用户角色 (admin/user)
- `auth_username`: 用户名
- `auth_remember`: 是否记住登录
- `auth_expires`: 过期时间

### 兼容性
- 自动清理旧版本的 `admin_token`
- 支持平滑升级

## 安全特性

1. **Token过期检查**：每次访问都检查token是否过期
2. **自动清理**：过期状态自动清除
3. **权限验证**：基于角色的权限控制
4. **安全跳转**：防止未授权访问

## 扩展说明

### 1. 添加新角色
在 `utils/auth.js` 中添加新的角色常量，并在登录页面中添加相应的模式。

### 2. 自定义过期时间
修改 `generateToken()` 函数中的过期时间计算逻辑。

### 3. 集成真实后端
替换 `api/auth.js` 中的模拟接口为真实的API调用。

### 4. 添加更多权限控制
使用 `hasPermission()` 函数在组件中进行细粒度权限控制。 