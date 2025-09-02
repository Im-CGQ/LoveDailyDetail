#!/bin/bash

# 设置错误时退出
set -e

echo "开始初始化数据库..."

# 数据库配置
DB_CONTAINER_NAME="mysql-latest"
DB_NAME="love_diary"
DB_USER="root"
DB_PASSWORD="cgqcgq250"
DB_PORT="3306"

# 检查MySQL容器是否运行
echo "检查MySQL容器状态..."
if ! docker ps | grep -q $DB_CONTAINER_NAME; then
    echo "MySQL容器未运行，正在启动..."
    
    # 停止并删除旧容器（如果存在）
    docker stop $DB_CONTAINER_NAME || true
    docker rm $DB_CONTAINER_NAME || true
    
    # 启动MySQL容器
    docker run -d \
        --name $DB_CONTAINER_NAME \
        -e MYSQL_ROOT_PASSWORD=$DB_PASSWORD \
        -e MYSQL_DATABASE=$DB_NAME \
        -e MYSQL_ROOT_HOST=% \
        -p $DB_PORT:3306 \
        mysql:8.0
        --bind-address=0.0.0.0
    
    echo "等待MySQL服务启动..."
    sleep 30
else
    echo "MySQL容器已在运行"
fi

# 等待MySQL服务完全启动
echo "等待MySQL服务就绪..."
until docker exec $DB_CONTAINER_NAME mysqladmin ping -h"localhost" -u$DB_USER -p$DB_PASSWORD --silent; do
    echo "等待MySQL服务启动..."
    sleep 5
done

echo "MySQL服务已就绪"

# 执行初始化SQL文件
echo "执行数据库初始化脚本..."
docker exec -i $DB_CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME < ./database/init.sql

echo "数据库初始化完成！"

# 查询数据库中的表
echo "查询数据库中的表..."
echo "=========================================="
echo "数据库表列表："
docker exec $DB_CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD -e "USE $DB_NAME; SHOW TABLES;"

echo ""
echo "=========================================="
echo "表结构详情："
docker exec $DB_CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD -e "USE $DB_NAME; SHOW TABLE STATUS;"


echo ""
echo "=========================================="
echo "数据库容器名称: $DB_CONTAINER_NAME"
echo "数据库名称: $DB_NAME"
echo "端口映射: $DB_PORT:3306"
echo "默认管理员账户: admin/admin"

