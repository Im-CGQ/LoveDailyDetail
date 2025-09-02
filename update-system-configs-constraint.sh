#!/bin/bash

echo "正在更新system_configs表约束..."
echo

# 检查MySQL是否运行
if ! command -v mysql &> /dev/null; then
    echo "错误: 未找到MySQL客户端，请确保MySQL已安装并添加到PATH"
    exit 1
fi

# 执行SQL更新脚本
echo "执行SQL更新脚本..."
mysql -u root -p < database/update_system_configs_constraint.sql

if [ $? -eq 0 ]; then
    echo "成功: system_configs表约束已更新"
    echo "现在每个用户都可以有自己的配置了"
else
    echo "错误: 数据库更新失败"
    exit 1
fi
