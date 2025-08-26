# 分享过期时间功能实现

## 功能概述

为系统添加了可配置的分享链接过期时间功能，管理员可以在系统配置中设置分享链接的过期时间，前端分享功能会根据配置生成对应过期时间的分享链接。

## 实现的功能

### 1. 系统配置管理

#### 前端配置界面 (`SystemConfigList.vue`)
- 添加了"分享设置"配置组
- 提供分享过期时间选择器，支持以下选项：
  - 30分钟
  - 1小时
  - 2小时
  - 6小时
  - 12小时
  - 24小时
  - 48小时
  - 72小时

#### 后端配置接口
- **获取分享过期时间**: `GET /system-config/share-expire-minutes`
- **设置分享过期时间**: `POST /system-config/share-expire-minutes`

### 2. 分享功能增强

#### 前端分享功能 (`Detail.vue`)
- 点击分享按钮时实际创建分享链接
- 自动复制分享链接到剪贴板
- 显示分享链接的过期时间信息
- 支持降级处理（不支持剪贴板时显示链接）

#### 后端分享逻辑 (`ShareServiceImpl.java`)
- 修改分享链接创建逻辑，使用系统配置中的过期时间
- 日记分享和信件分享都使用统一的过期时间配置
- 默认过期时间为60分钟（如果未配置）

### 3. 数据库配置

分享过期时间配置存储在 `system_config` 表中：
- **配置键**: `share_expire_minutes`
- **配置类型**: `NUMBER`
- **描述**: "分享链接过期时间（分钟）"
- **默认值**: 60分钟

## 技术实现

### 前端实现

1. **系统配置页面**:
   ```javascript
   // 过期时间选项
   const expireOptions = [
     { text: '30分钟', value: 30 },
     { text: '1小时', value: 60 },
     { text: '2小时', value: 120 },
     // ... 更多选项
   ]
   ```

2. **分享功能**:
   ```javascript
   const share = async () => {
     // 创建分享链接
     const shareData = await createShareLink(diaryId)
     
     // 获取过期时间配置
     const minutes = await getShareExpireMinutes()
     
     // 复制到剪贴板
     await navigator.clipboard.writeText(fullShareUrl)
   }
   ```

### 后端实现

1. **系统配置服务**:
   ```java
   @Override
   public Integer getShareExpireMinutes() {
       SystemConfigDTO config = getConfigByKey("share_expire_minutes");
       return config != null ? Integer.parseInt(config.getConfigValue()) : 60;
   }
   ```

2. **分享服务**:
   ```java
   // 从系统配置获取分享过期时间（分钟）
   Integer expireMinutes = systemConfigService.getShareExpireMinutes();
   // 设置过期时间
   LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expireMinutes);
   ```

## 使用流程

1. **管理员配置过期时间**:
   - 进入系统配置页面
   - 在"分享设置"中选择合适的过期时间
   - 保存配置

2. **用户分享内容**:
   - 在日记详情页面点击分享按钮
   - 系统自动创建分享链接并复制到剪贴板
   - 显示分享链接的过期时间信息

3. **分享链接访问**:
   - 其他用户通过分享链接访问内容
   - 系统自动验证链接是否过期
   - 过期后链接自动失效

## 配置说明

- **配置位置**: 系统配置 → 分享设置 → 分享链接过期时间
- **配置范围**: 全局配置，影响所有分享链接
- **生效时间**: 配置修改后立即生效，新创建的分享链接使用新配置
- **历史链接**: 已创建的分享链接不受配置修改影响，仍按原过期时间执行

## 注意事项

1. 分享过期时间配置为全局配置，影响所有用户
2. 修改配置后，新创建的分享链接使用新配置，已创建的链接不受影响
3. 分享链接过期后自动失效，无法访问
4. 系统会定期清理过期的分享链接记录
