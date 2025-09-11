#!/bin/bash

# 通用分享链接迁移脚本
# 将 diary_share_links 和 letter_share_links 合并为 universal_share_links

echo "开始迁移到通用分享链接系统..."

# 检查数据库连接
if [ -z "$DB_HOST" ] || [ -z "$DB_NAME" ] || [ -z "$DB_USER" ]; then
    echo "错误: 请设置环境变量 DB_HOST, DB_NAME, DB_USER"
    echo "示例: export DB_HOST=localhost DB_NAME=lovediary DB_USER=root"
    exit 1
fi

# 设置默认端口
DB_PORT=${DB_PORT:-3306}

echo "数据库连接信息:"
echo "  主机: $DB_HOST:$DB_PORT"
echo "  数据库: $DB_NAME"
echo "  用户: $DB_USER"

# 备份数据库
echo "正在备份数据库..."
BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
mysqldump -h $DB_HOST -P $DB_PORT -u $DB_USER -p $DB_NAME > $BACKUP_FILE

if [ $? -eq 0 ]; then
    echo "数据库备份成功: $BACKUP_FILE"
else
    echo "错误: 数据库备份失败"
    exit 1
fi

# 执行迁移脚本
echo "正在执行迁移脚本..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p $DB_NAME < database/migrate_to_universal_share_links.sql

if [ $? -eq 0 ]; then
    echo "迁移成功完成!"
    echo ""
    echo "下一步:"
    echo "1. 测试现有功能是否正常"
    echo "2. 确认无误后可以删除原表:"
    echo "   DROP TABLE diary_share_links;"
    echo "   DROP TABLE letter_share_links;"
    echo "3. 备份文件: $BACKUP_FILE"
else
    echo "错误: 迁移失败"
    echo "请检查数据库连接和权限"
    exit 1
fi
