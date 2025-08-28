#!/bin/bash

# 数据库更新脚本 - 添加用户头像字段
echo "开始更新数据库，添加用户头像字段..."

# 数据库配置
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="love_diary"
DB_USER="root"
DB_PASSWORD=""

# 检查MySQL是否运行
if ! mysqladmin ping -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" --silent; then
    echo "错误: 无法连接到MySQL数据库，请确保MySQL服务正在运行"
    exit 1
fi

# 执行数据库更新
echo "执行数据库更新脚本..."
mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < database/update_user_avatar_simple.sql

if [ $? -eq 0 ]; then
    echo "✅ 数据库更新成功！用户头像字段已添加"
else
    echo "❌ 数据库更新失败"
    exit 1
fi

echo "数据库更新完成！"
