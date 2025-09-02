#!/bin/bash

# 设置错误时退出
set -e

echo "开始备份数据库..."

# 数据库配置
DB_CONTAINER_NAME="mysql-latest"
DB_NAME="love_diary"
DB_USER="root"
DB_PASSWORD="cgqcgq250"
DB_PORT="3306"

# 检查MySQL容器是否运行
echo "检查MySQL容器状态..."
if ! docker ps | grep -q $DB_CONTAINER_NAME; then
    echo "错误：MySQL容器未运行，请先运行 init-database.sh 脚本"
    exit 1
fi

# 等待MySQL服务完全启动
echo "等待MySQL服务就绪..."
until docker exec $DB_CONTAINER_NAME mysqladmin ping -h"localhost" -u$DB_USER -p$DB_PASSWORD --silent; do
    echo "等待MySQL服务启动..."
    sleep 5
done

echo "MySQL服务已就绪"

# 创建备份目录
BACKUP_DIR="./database/backups"
mkdir -p $BACKUP_DIR

# 创建数据库备份
echo "创建数据库备份..."
BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
docker exec $DB_CONTAINER_NAME mysqldump -u$DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_DIR/$BACKUP_FILE
echo "数据库备份已保存到: $BACKUP_DIR/$BACKUP_FILE"

# 显示备份文件信息
echo "备份文件信息："
ls -lh $BACKUP_DIR/$BACKUP_FILE

echo ""
echo "数据库备份完成！"
echo "=========================================="
echo "数据库容器名称: $DB_CONTAINER_NAME"
echo "数据库名称: $DB_NAME"
echo "端口映射: $DB_PORT:3306"
echo "备份文件: ./database/$BACKUP_FILE"
