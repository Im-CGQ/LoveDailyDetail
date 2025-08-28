#!/bin/bash

# 数据库更新脚本 - 添加手机号字段
echo "开始更新数据库，添加手机号字段..."

# 数据库连接信息
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="love_diary"
DB_USER="root"
DB_PASSWORD="123456"

# 执行SQL脚本
echo "执行数据库更新脚本..."
mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME < database/update_user_phone.sql

if [ $? -eq 0 ]; then
    echo "✅ 数据库更新成功！手机号字段已添加。"
    echo "📝 注意事项："
    echo "   - 新用户注册时需要提供手机号和验证码"
    echo "   - 现有用户可以通过用户名密码登录"
    echo "   - 手机号登录功能已启用，验证码为：123456"
else
    echo "❌ 数据库更新失败！"
    exit 1
fi

echo "数据库更新完成！"
