# LoveDaily 部署脚本使用指南

## 📦 脚本文件说明

本项目提供了多个部署脚本，适用于不同的部署场景：

### 1. `one-click-deploy.sh` - 一键部署脚本（推荐）
**功能最完整，适合全新服务器部署**

- ✅ 自动安装Git、Docker、Docker Compose
- ✅ 自动配置SSH密钥和GitHub连接
- ✅ 自动克隆项目并部署
- ✅ 完整的系统检查和错误处理

**使用方法：**
```bash
# 给脚本执行权限
chmod +x one-click-deploy.sh

# 运行脚本（会提示输入GitHub信息）
./one-click-deploy.sh

# 或者直接指定GitHub用户名和仓库名
./one-click-deploy.sh yourusername LoveDailyDetail
```

### 2. `auto-deploy.sh` - 自动化部署脚本
**适合已有Git环境的服务器**

- ✅ 检查Git安装和配置
- ✅ 自动生成SSH密钥
- ✅ 项目文件完整性检查
- ✅ 详细的日志输出

**使用方法：**
```bash
chmod +x auto-deploy.sh
./auto-deploy.sh
```

### 3. `quick-deploy.sh` - 快速部署脚本
**适合已有完整环境的服务器**

- ✅ 快速克隆和部署
- ✅ 最小化交互
- ✅ 适合自动化部署

**使用方法：**
```bash
chmod +x quick-deploy.sh
./quick-deploy.sh yourusername LoveDailyDetail
```

### 4. `install-docker.sh` - Docker安装脚本
**仅安装Docker环境**

- ✅ 自动检测系统类型
- ✅ 安装Docker和Docker Compose
- ✅ 配置用户权限

**使用方法：**
```bash
chmod +x install-docker.sh
./install-docker.sh
```

### 5. `deploy.sh` - 项目部署脚本
**项目内部的Docker部署脚本**

- ✅ 启动Docker服务
- ✅ 构建和运行容器
- ✅ 健康检查

## 🚀 推荐部署流程

### 全新服务器部署（推荐）

1. **上传脚本到服务器**
```bash
# 将脚本上传到服务器
scp *.sh user@server:/home/user/
```

2. **运行一键部署**
```bash
# 连接到服务器
ssh user@server

# 进入脚本目录
cd /home/user

# 运行一键部署脚本
chmod +x one-click-deploy.sh
./one-click-deploy.sh
```

3. **按提示操作**
- 输入GitHub用户名和邮箱
- 添加SSH公钥到GitHub
- 等待自动部署完成

### 已有环境快速部署

```bash
# 快速部署
chmod +x quick-deploy.sh
./quick-deploy.sh yourusername LoveDailyDetail
```

## 📋 部署前准备

### 1. GitHub准备
- 确保项目已上传到GitHub
- 准备好GitHub用户名和邮箱
- 确保有SSH密钥或准备生成

### 2. 服务器要求
- **操作系统**: Ubuntu 20.04+ / CentOS 7+
- **内存**: 至少2GB可用内存
- **磁盘**: 至少5GB可用空间
- **网络**: 80端口和40000端口可访问

### 3. 权限要求
- 非root用户（脚本会自动处理权限）
- sudo权限（用于安装软件包）

## 🔧 脚本功能详解

### 系统检查
- 内存和磁盘空间检查
- 端口占用检查
- 系统类型检测

### 环境安装
- Git自动安装和配置
- Docker和Docker Compose安装
- SSH密钥生成和配置

### 项目部署
- 项目代码克隆/更新
- 文件完整性检查
- Docker容器构建和启动
- 服务健康检查

### 结果展示
- 访问地址显示
- 管理命令提示
- 日志查看选项

## 🐛 常见问题解决

### 1. SSH密钥问题
```bash
# 手动生成SSH密钥
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"

# 添加到ssh-agent
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa

# 查看公钥
cat ~/.ssh/id_rsa.pub
```

### 2. Docker权限问题
```bash
# 添加用户到docker组
sudo usermod -aG docker $USER

# 重新登录或运行
newgrp docker
```

### 3. 端口占用问题
```bash
# 检查端口占用
netstat -tulpn | grep :80
netstat -tulpn | grep :40000

# 停止占用端口的服务
sudo systemctl stop nginx  # 如果nginx占用80端口
```

### 4. 内存不足问题
```bash
# 检查内存使用
free -h

# 清理系统缓存
sudo sync && sudo sysctl -w vm.drop_caches=3
```

## 📞 技术支持

### 查看日志
```bash
# 查看部署日志
cd LoveDailyDetail/docker
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### 重启服务
```bash
cd LoveDailyDetail/docker
docker-compose restart
```

### 完全重新部署
```bash
cd LoveDailyDetail/docker
docker-compose down
docker-compose up -d --build
```

## 🎯 部署成功标志

部署成功后，你应该能够：

1. **访问前端**: http://localhost
2. **访问后端**: http://localhost:40000/api
3. **查看服务状态**: `docker-compose ps` 显示所有服务为 `Up` 状态
4. **查看日志**: 无错误信息

## 📝 注意事项

1. **不要使用root用户运行脚本**
2. **确保网络连接正常**
3. **首次部署需要较长时间**（下载镜像和构建）
4. **建议在部署前备份重要数据**
5. **生产环境请修改默认密码**

---

通过这些脚本，你可以轻松地在Linux服务器上部署LoveDaily项目！

