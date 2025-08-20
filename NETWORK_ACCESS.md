# 网络访问配置说明 🌐

## 问题描述
Vue 开发服务器默认只绑定到 `localhost`，无法通过 IP 地址访问，导致局域网内其他设备无法访问。

## 解决方案

### 1. 修改 Vite 配置
在 `frontend/vite.config.js` 中添加 `host: '0.0.0.0'`：

```javascript
export default defineConfig({
  // ... 其他配置
  server: {
    port: 3000,
    host: '0.0.0.0',  // 允许所有 IP 访问
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

### 2. 获取本机 IP 地址

#### Windows 系统：
```cmd
ipconfig
```
查找 "IPv4 地址" 字段，通常是 `192.168.x.x` 格式。

#### macOS/Linux 系统：
```bash
ifconfig
# 或
ip addr show
```

### 3. 访问地址

启动项目后，可以通过以下地址访问：

#### 本机访问：
- 前端：`http://localhost:3000`
- 后端：`http://localhost:8080`

#### 局域网访问：
- 前端：`http://你的IP地址:3000`
- 后端：`http://你的IP地址:8080`

例如：
- 前端：`http://192.168.1.100:3000`
- 后端：`http://192.168.1.100:8080`

## 防火墙设置

### Windows 防火墙：
1. 打开 "Windows Defender 防火墙"
2. 点击 "允许应用或功能通过 Windows Defender 防火墙"
3. 添加 Node.js 和 Java 到允许列表

### macOS 防火墙：
1. 系统偏好设置 → 安全性与隐私 → 防火墙
2. 点击 "防火墙选项"
3. 添加 Node.js 和 Java 到允许列表

## 常见问题

### 1. 端口被占用
如果 3000 端口被占用，可以修改 `vite.config.js`：
```javascript
server: {
  port: 3001,  // 改为其他端口
  host: '0.0.0.0'
}
```

### 2. 无法访问后端 API
确保后端服务也配置了正确的网络访问：
```yaml
# application.yml
server:
  port: 8080
  address: 0.0.0.0  # 允许所有 IP 访问
```

### 3. 移动端访问
- 确保手机和电脑在同一个 WiFi 网络
- 使用电脑的 IP 地址访问
- 某些路由器可能阻止局域网访问，需要在路由器设置中允许

## 安全注意事项

⚠️ **重要提醒：**
- `host: '0.0.0.0'` 允许所有网络接口访问
- 仅用于开发环境，生产环境请使用更安全的配置
- 确保防火墙正确配置，避免安全风险

## 测试方法

1. **启动项目**：
   ```bash
   # 使用启动脚本
   start.bat
   
   # 或手动启动
   cd frontend && npm run dev
   cd backend && mvn spring-boot:run
   ```

2. **获取 IP 地址**：
   ```cmd
   ipconfig
   ```

3. **测试访问**：
   - 本机：`http://localhost:3000`
   - 局域网：`http://你的IP:3000`
   - 手机：在手机浏览器中输入 `http://你的IP:3000`

## 成功标志

- ✅ 本机可以正常访问
- ✅ 局域网内其他设备可以访问
- ✅ 移动端可以正常访问
- ✅ 所有功能正常工作

---

**配置完成后，重启开发服务器即可生效！** 🎉 