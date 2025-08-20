# OSS上传功能配置指南

## 概述

本项目已配置前端直接上传文件到阿里云OSS的功能，避免了文件先上传到后端再转发到OSS的中间步骤，提高了上传效率和用户体验。

## 后端配置

### 1. 配置文件

在 `backend/src/main/resources/application.yml` 中配置阿里云OSS参数：

```yaml
aliyun:
  oss:
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    bucket-name: your-bucket-name
    url-prefix: https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com/
```

### 2. 核心组件

- **OssConfig**: OSS配置类，读取配置文件中的参数
- **OssSignatureService**: OSS签名服务接口
- **OssSignatureServiceImpl**: OSS签名服务实现，生成前端上传所需的签名信息
- **AdminController**: 提供获取OSS签名的API接口

### 3. API接口

#### 获取OSS上传签名

```
GET /api/admin/oss/signature?fileName=test.jpg&fileType=image
```

**参数：**
- `fileName`: 文件名
- `fileType`: 文件类型 (image, video, music)

**返回：**
```json
{
  "success": true,
  "message": "获取上传签名成功",
  "data": {
    "accessid": "your-access-key-id",
    "policy": "base64-encoded-policy",
    "signature": "base64-encoded-signature",
    "dir": "images/",
    "host": "https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com",
    "expire": "1640995200",
    "fileName": "image/1640995200_abc123.jpg",
    "urlPrefix": "https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com/"
  }
}
```

## 前端配置

### 1. 上传服务

`frontend/src/api/upload.js` 提供了以下功能：

- `getOssSignature(fileName, fileType)`: 获取OSS上传签名
- `uploadToOss(file, fileType)`: 直接上传文件到OSS
- `uploadImage(file)`: 上传图片
- `uploadVideo(file)`: 上传视频
- `uploadMusic(file)`: 上传音乐

### 2. 使用示例

```javascript
import { uploadImage, uploadVideo, uploadMusic } from '@/api/upload.js'

// 上传图片
const imageUrl = await uploadImage(file)

// 上传视频
const videoUrl = await uploadVideo(file)

// 上传音乐
const musicUrl = await uploadMusic(file)
```

### 3. 组件集成

已修改以下组件使用新的上传服务：

- `CreateDiary.vue`: 创建日记页面
- `EditDiary.vue`: 编辑日记页面

## 测试

### 1. 测试页面

使用 `test-oss-upload.html` 测试OSS上传功能：

1. 启动后端服务
2. 在浏览器中打开 `test-oss-upload.html`
3. 选择文件并点击上传按钮
4. 查看上传结果

### 2. 测试步骤

1. 确保后端服务正常运行
2. 确保阿里云OSS配置正确
3. 测试不同类型的文件上传
4. 验证文件URL是否可以正常访问

## 安全考虑

### 1. 签名有效期

- 签名有效期为1小时
- 过期后需要重新获取签名

### 2. 文件大小限制

- 策略中设置了100MB的文件大小限制
- 可根据需要在后端调整

### 3. 文件类型限制

- 通过前端文件选择器限制文件类型
- 后端策略中可进一步限制文件路径

## 故障排除

### 1. 常见错误

- **签名错误**: 检查AccessKey配置
- **上传失败**: 检查Bucket权限和网络连接
- **文件过大**: 调整策略中的文件大小限制

### 2. 调试方法

1. 检查浏览器控制台错误信息
2. 查看后端日志
3. 使用测试页面验证功能

## 注意事项

1. 请确保阿里云OSS的AccessKey安全，不要提交到代码仓库
2. 建议使用RAM用户的AccessKey，并设置最小权限
3. 生产环境中建议使用HTTPS
4. 定期检查OSS存储使用情况和费用 