# OSS权限配置指南

## 403 Forbidden 错误解决方案

### 1. 检查AccessKey权限

确保AccessKey具有以下权限：
- `oss:PutObject` - 上传文件权限
- `oss:GetObject` - 读取文件权限
- `oss:ListObjects` - 列出文件权限

### 2. Bucket权限配置

在阿里云OSS控制台中配置Bucket权限：

#### 2.1 读写权限
- 进入Bucket管理页面
- 点击"权限管理" -> "读写权限"
- 选择"公共读"或"公共读写"（开发环境）
- 生产环境建议使用"私有"

#### 2.2 跨域设置
- 点击"权限管理" -> "跨域设置"
- 添加跨域规则：
  - 来源：`*` 或具体域名
  - 允许Methods：`GET, POST, PUT, DELETE, HEAD`
  - 允许Headers：`*`
  - 暴露Headers：`ETag`
  - 缓存时间：`86400`

### 3. RAM用户权限配置

如果使用RAM用户，确保分配了正确的权限策略：

```json
{
    "Version": "1",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "oss:PutObject",
                "oss:GetObject",
                "oss:ListObjects"
            ],
            "Resource": [
                "acs:oss:*:*:love-diary-detail",
                "acs:oss:*:*:love-diary-detail/*"
            ]
        }
    ]
}
```

### 4. 测试步骤

1. 使用 `test-access-key.html` 测试基本访问权限
2. 检查Bucket是否允许公共访问
3. 验证跨域配置是否正确
4. 确认AccessKey权限是否足够

### 5. 常见问题

#### 5.1 AccessKey权限不足
- 检查AccessKey是否为主账号AccessKey
- 如果是RAM用户，检查权限策略配置

#### 5.2 Bucket权限问题
- 确保Bucket存在且名称正确
- 检查Bucket的读写权限设置

#### 5.3 跨域问题
- 确保CORS配置允许POST方法
- 检查来源域名是否在允许列表中

### 6. 安全建议

1. **生产环境**：
   - 使用RAM用户而不是主账号AccessKey
   - 设置最小权限原则
   - 定期轮换AccessKey

2. **开发环境**：
   - 可以临时设置为公共读写权限
   - 使用测试用的AccessKey

### 7. 调试工具

- `test-access-key.html` - 测试基本访问权限
- `test-oss-debug.html` - 调试上传问题
- 浏览器开发者工具 - 查看网络请求详情 