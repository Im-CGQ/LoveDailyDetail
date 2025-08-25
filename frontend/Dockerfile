# 使用nginx作为基础镜像
FROM nginx:alpine

# 设置工作目录
WORKDIR /app

# 复制构建好的dist文件到nginx目录
COPY dist /usr/share/nginx/html

# 复制nginx配置文件（如果有自定义配置）
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露80端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]