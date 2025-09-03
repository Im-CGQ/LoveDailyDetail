#!/bin/bash

echo "正在更新数据库，添加duration字段..."
echo

# 检查MySQL是否安装
if ! command -v mysql &> /dev/null; then
    echo "错误：MySQL未安装或不在PATH中"
    echo "请确保MySQL已安装并添加到系统PATH"
    exit 1
fi

# 执行数据库更新脚本
echo "执行数据库更新脚本..."
mysql -u root -p < database/add_duration_seconds_field.sql

if [ $? -eq 0 ]; then
    echo "数据库更新成功！"
    echo "duration字段已添加到movies表"
else
    echo "数据库更新失败！"
    exit 1
fi

echo
echo "更新完成！"
