# Admin 后台管理 API 指南

## 概述

Admin 后台管理系统提供了完整的日记管理功能，包括统计、创建、编辑、删除等操作。所有接口都通过 `/admin` 前缀访问。

## 接口列表

### 1. 获取统计数据

**接口地址：** `GET /admin/stats`

**功能：** 获取后台统计数据，包括总日记数、本月新增、今年新增、总图片数、总视频数等。

**响应示例：**
```json
{
  "success": true,
  "message": "获取统计数据成功",
  "data": {
    "totalDiaries": 25,
    "thisMonthDiaries": 8,
    "thisYearDiaries": 45,
    "totalImages": 120,
    "totalVideos": 15
  }
}
```

### 2. 分页获取日记列表

**接口地址：** `GET /admin/diaries?page={page}&size={size}`

**参数：**
- `page`: 页码（默认1）
- `size`: 每页大小（默认10）

**响应示例：**
```json
{
  "success": true,
  "message": "获取日记列表成功",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "我们的第一次约会",
        "date": "2024-01-15",
        "description": "今天是我们第一次约会...",
        "images": ["url1", "url2"],
        "videos": ["video_url1", "video_url2"],
        "backgroundMusic": "music_url",
        "createdAt": "2024-01-15T10:00:00",
        "updatedAt": "2024-01-15T10:00:00"
      }
    ],
    "totalElements": 25,
    "totalPages": 3,
    "currentPage": 1,
    "size": 10
  }
}
```

### 3. 获取最近日记

**接口地址：** `GET /admin/diaries/recent?limit={limit}`

**参数：**
- `limit`: 获取数量限制（默认5）

**响应示例：**
```json
{
  "success": true,
  "message": "获取最近日记成功",
  "data": [
    {
      "id": 1,
      "title": "我们的第一次约会",
      "date": "2024-01-15"
    }
  ]
}
```

### 4. 获取日记详情

**接口地址：** `GET /admin/diaries/{id}`

**参数：**
- `id`: 日记ID

**响应示例：**
```json
{
  "success": true,
  "message": "获取日记详情成功",
  "data": {
    "id": 1,
    "title": "我们的第一次约会",
    "date": "2024-01-15",
    "description": "今天是我们第一次约会...",
    "images": ["url1", "url2"],
    "video": "video_url",
    "backgroundMusic": "music_url",
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-01-15T10:00:00"
  }
}
```

### 5. 创建日记

**接口地址：** `POST /admin/diaries`

**请求体：**
```json
{
  "title": "日记标题",
  "date": "2024-12-01",
  "description": "日记描述",
  "images": ["image_url1", "image_url2"],
  "videos": ["video_url1", "video_url2"],
  "backgroundMusic": "music_url"
}
```

**响应示例：**
```json
{
  "success": true,
  "message": "创建日记成功",
  "data": {
    "id": 26,
    "title": "日记标题",
    "date": "2024-12-01",
    "description": "日记描述",
    "images": ["image_url1", "image_url2"],
    "videos": ["video_url1", "video_url2"],
    "backgroundMusic": "music_url",
    "createdAt": "2024-12-01T10:00:00",
    "updatedAt": "2024-12-01T10:00:00"
  }
}
```

### 6. 更新日记

**接口地址：** `PUT /admin/diaries/{id}`

**参数：**
- `id`: 日记ID

**请求体：** 同创建日记

**响应示例：**
```json
{
  "success": true,
  "message": "更新日记成功",
  "data": {
    "id": 1,
    "title": "更新后的标题",
    "date": "2024-12-01",
    "description": "更新后的描述",
    "images": ["new_image_url"],
    "videos": ["new_video_url1", "new_video_url2"],
    "backgroundMusic": "new_music_url",
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-12-01T10:00:00"
  }
}
```

### 7. 删除日记

**接口地址：** `DELETE /admin/diaries/{id}`

**参数：**
- `id`: 日记ID

**响应示例：**
```json
{
  "success": true,
  "message": "删除日记成功",
  "data": "日记已删除"
}
```

### 8. 文件上传

**接口地址：** `POST /admin/upload`

**参数：**
- `file`: 文件（MultipartFile）
- `type`: 文件类型（image/video/audio）

**响应示例：**
```json
{
  "success": true,
  "message": "文件上传成功",
  "data": {
    "url": "https://oss.example.com/file.jpg"
  }
}
```

## 前端集成

### 1. API 文件

前端使用 `frontend/src/api/admin.js` 文件来调用这些接口：

```javascript
import { 
  getAdminStats, 
  getDiariesWithPagination, 
  getRecentDiaries,
  getDiaryById,
  createDiary,
  updateDiary,
  deleteDiary,
  uploadFile 
} from '@/api/admin.js'
```

### 2. 组件更新

所有 admin 相关的 Vue 组件都已更新为使用真实 API：

- `AdminHome.vue`: 显示统计数据和最近日记
- `DiaryList.vue`: 分页显示日记列表，支持删除操作
- `CreateDiary.vue`: 创建新日记，支持文件上传
- `EditDiary.vue`: 编辑现有日记，支持文件上传

### 3. 错误处理

所有 API 调用都包含错误处理，当后端不可用时会自动使用 mock 数据：

```javascript
try {
  const result = await getAdminStats()
  // 处理成功结果
} catch (error) {
  console.error('API调用失败:', error)
  // 使用mock数据或显示错误信息
}
```

## 测试

可以使用 `test-admin-api.html` 文件来测试所有 admin 接口：

1. 启动后端服务
2. 在浏览器中打开 `test-admin-api.html`
3. 点击各个测试按钮验证接口功能

## 注意事项

1. **认证：** 所有 admin 接口都需要用户登录
2. **文件上传：** 支持图片、视频、音频文件上传到阿里云 OSS
3. **分页：** 日记列表支持分页，避免数据量过大
4. **错误处理：** 前端包含完整的错误处理和用户提示
5. **Mock 数据：** 当后端不可用时，前端会自动使用 mock 数据

## 部署

1. 确保后端服务正常运行在 `http://localhost:8080`
2. 确保数据库已初始化并包含示例数据
3. 确保阿里云 OSS 配置正确（用于文件上传）
4. 启动前端服务：`npm run dev`
5. 访问 `http://localhost:3000/admin` 进入后台管理

## 故障排除

### 常见问题

1. **接口返回 404：** 检查后端服务是否启动
2. **文件上传失败：** 检查阿里云 OSS 配置
3. **数据库连接失败：** 检查数据库配置和连接
4. **跨域问题：** 检查后端 CORS 配置

### 日志查看

- 后端日志：查看控制台输出
- 前端日志：打开浏览器开发者工具查看 Console
- 网络请求：在 Network 标签页查看 API 调用详情 