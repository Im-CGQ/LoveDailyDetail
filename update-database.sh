#!/bin/bash

# 数据库更新脚本：添加分享功能（Docker版本）
# 使用方法：./update-database.sh [容器名] [数据库用户名] [数据库名]
# 例如：./update-database.sh mysql-container root love_diary

# 默认参数
CONTAINER_NAME=${1:-"mysql-latest"}
DB_USER=${2:-"root"}
DB_NAME=${3:-"love_diary"}

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== 数据库更新脚本：添加分享功能（Docker版本） ===${NC}"
echo "Docker容器名: $CONTAINER_NAME"
echo "数据库用户: $DB_USER"
echo "数据库名称: $DB_NAME"
echo ""

# 检查SQL文件是否存在
SQL_FILE="database/update_to_share_feature.sql"
if [ ! -f "$SQL_FILE" ]; then
    echo -e "${RED}错误: SQL文件不存在: $SQL_FILE${NC}"
    exit 1
fi

# 提示用户确认
echo -e "${YELLOW}警告: 此脚本将修改数据库结构，请确保已备份数据库${NC}"
read -p "是否继续执行? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}操作已取消${NC}"
    exit 0
fi

# 检查Docker容器是否存在
echo -e "${GREEN}检查Docker容器状态...${NC}"
if ! docker ps | grep -q "$CONTAINER_NAME"; then
    echo -e "${RED}错误: Docker容器 '$CONTAINER_NAME' 未运行或不存在${NC}"
    echo "请检查容器名称是否正确，或者使用以下命令查看运行中的容器："
    echo "docker ps"
    exit 1
fi

# 执行SQL脚本
echo -e "${GREEN}开始执行数据库更新...${NC}"
docker exec -i "$CONTAINER_NAME" mysql -u"$DB_USER" -p "$DB_NAME" < "$SQL_FILE"

# 检查执行结果
if [ $? -eq 0 ]; then
    echo -e "${GREEN}数据库更新成功！${NC}"
    echo ""
    echo -e "${GREEN}新增的表:${NC}"
    echo "- diary_share_links (日记分享链接表)"
    echo "- letter_share_links (信件分享链接表)"
    echo ""
    echo -e "${GREEN}新增的索引:${NC}"
    echo "- idx_diary_share_links_token"
    echo "- idx_diary_share_links_diary_id"
    echo "- idx_diary_share_links_expires_at"
    echo "- idx_letter_share_links_token"
    echo "- idx_letter_share_links_letter_id"
    echo "- idx_letter_share_links_expires_at"
else
    echo -e "${RED}数据库更新失败！请检查错误信息${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}更新完成！现在可以部署新的后端和前端代码了。${NC}"
