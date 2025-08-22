#!/bin/bash

# 修复脚本文件的换行符问题
echo "🔧 修复脚本文件换行符..."

# 修复所有脚本文件
for script in *.sh; do
    if [ -f "$script" ]; then
        echo "修复文件: $script"
        # 使用sed命令替换Windows换行符为Unix换行符
        sed -i 's/\r$//' "$script"
    fi
done

echo "✅ 修复完成！现在可以运行脚本了"
echo "运行命令: ./one-click-deploy.sh"

