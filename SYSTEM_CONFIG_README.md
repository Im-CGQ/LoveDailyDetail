# 系统配置功能说明

## 功能概述

系统配置功能允许管理员自定义应用的各种设置，目前支持以下配置：

### 1. 在一起时间配置 (together_date)
- **功能**: 设置两个人在一起的时间
- **类型**: 日期字符串 (YYYY-MM-DD)
- **默认值**: 2024-01-01
- **用途**: 用于计算在一起的天数，在日历等页面显示

### 2. 背景音乐自动播放配置 (background_music_autoplay)
- **功能**: 控制背景音乐是否自动播放
- **类型**: 布尔值 (true/false)
- **默认值**: true
- **用途**: 控制日记页面背景音乐的自动播放行为

## 数据库结构

### system_configs 表
```sql
CREATE TABLE system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型：STRING, NUMBER, BOOLEAN, JSON',
    description VARCHAR(255) COMMENT '配置描述',
    user_id BIGINT NULL COMMENT '用户ID，NULL表示全局配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

## 后端API

### 主要接口
- `GET /api/system-config/together-date` - 获取在一起时间
- `POST /api/system-config/together-date` - 设置在一起时间
- `GET /api/system-config/background-music-autoplay` - 获取背景音乐自动播放配置
- `POST /api/system-config/background-music-autoplay` - 设置背景音乐自动播放配置
- `GET /api/system-config/all` - 获取所有配置
- `GET /api/system-config/global` - 获取全局配置
- `GET /api/system-config/user/{userId}` - 获取用户配置
- `POST /api/system-config/save` - 保存配置
- `DELETE /api/system-config/delete/{configKey}/{userId}` - 删除配置

## 前端功能

### 后台管理系统配置页面
- **路径**: `/admin/system-config`
- **功能**: 
  - 设置在一起的时间（日期选择器）
  - 切换背景音乐自动播放（开关）
  - 重置为默认配置

### 入口位置
- 后台管理页面的二级导航菜单中的"系统配置"选项
- 点击后跳转到系统配置管理页面

## 使用方法

### 1. 数据库更新
运行以下命令更新数据库：
```bash
# Windows
update-system-config.bat

# 或手动执行SQL
mysql -u root -p < backend/database/update_system_config.sql
```

### 2. 启动服务
```bash
# 启动后端
cd backend
mvn spring-boot:run

# 启动前端
cd frontend
npm run dev
```

### 3. 访问配置页面
1. 登录应用
2. 进入后台管理（点击Welcome页面右上角的⚙️按钮）
3. 在二级导航菜单中点击"系统配置"
4. 进行个性化设置

## 扩展配置

要添加新的系统配置，需要：

1. **数据库**: 在 `system_configs` 表中插入新配置
2. **后端**: 在 `SystemConfigService` 中添加对应的getter/setter方法
3. **前端**: 在 `SystemConfigList.vue` 中添加对应的UI组件
4. **API**: 在 `SystemConfigController` 中添加对应的接口

### 示例：添加新配置
```java
// 在 SystemConfigService 中添加
public String getNewConfig() {
    SystemConfigDTO config = getConfigByKey("new_config");
    return config != null ? config.getConfigValue() : "default_value";
}

public void setNewConfig(String value) {
    SystemConfigDTO config = new SystemConfigDTO();
    config.setConfigKey("new_config");
    config.setConfigValue(value);
    config.setConfigType("STRING");
    config.setDescription("新配置描述");
    config.setUserId(null);
    saveConfig(config);
}
```

## 注意事项

1. 配置分为全局配置（user_id为NULL）和用户配置（user_id不为NULL）
2. 用户配置会覆盖全局配置
3. 配置类型支持：STRING、NUMBER、BOOLEAN、JSON
4. 所有配置操作都有错误处理和用户提示
5. 配置页面需要管理员权限才能访问
6. 系统配置功能已从用户端移到后台管理，只有管理员可以修改
