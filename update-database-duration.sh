#!/bin/bash

echo "========================================"
echo "电影时长字段更新工具"
echo "========================================"
echo
echo "请选择操作："
echo "1. 完整重建数据库（推荐新环境）"
echo "2. 只添加duration字段（推荐现有环境）"
echo "3. 退出"
echo
read -p "请输入选择 (1-3): " choice

case $choice in
    1)
        echo
        echo "正在完整重建数据库..."
        echo "注意：这将删除所有现有数据！"
        echo
        read -p "确认要删除所有数据吗？(y/N): " confirm
        if [[ $confirm =~ ^[Yy]$ ]]; then
            full_rebuild
        else
            echo "操作已取消"
            exit 0
        fi
        ;;
    2)
        echo
        echo "正在添加duration字段到现有数据库..."
        echo
        add_fields_only
        ;;
    3)
        echo "操作已取消"
        exit 0
        ;;
    *)
        echo "无效选择，请重新运行脚本"
        exit 1
        ;;
esac

full_rebuild() {
    # 检查MySQL是否安装
    if ! command -v mysql &> /dev/null; then
        echo "错误：MySQL未安装或不在PATH中"
        echo "请确保MySQL已安装并添加到系统PATH"
        exit 1
    fi

    echo "执行完整数据库重建..."
    mysql -u root -p < database/init.sql

    if [ $? -eq 0 ]; then
        echo "数据库重建成功！"
        echo "所有表都已创建，包括duration字段"
    else
        echo "数据库重建失败！"
        exit 1
    fi
}

add_fields_only() {
    # 检查MySQL是否安装
    if ! command -v mysql &> /dev/null; then
        echo "错误：MySQL未安装或不在PATH中"
        echo "请确保MySQL已安装并添加到系统PATH"
        exit 1
    fi

    echo "执行字段添加脚本..."
    mysql -u root -p < database/add_duration_field_only.sql

    if [ $? -eq 0 ]; then
        echo "字段添加成功！"
        echo "duration字段已添加到movies表"
    else
        echo "字段添加失败！"
        exit 1
    fi
}

echo
echo "操作完成！"
