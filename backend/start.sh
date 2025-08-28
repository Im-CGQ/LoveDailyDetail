#!/bin/bash

echo "启动Love Diary后端服务..."

# 设置JVM内存参数
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请确保已安装Java 11或更高版本"
    exit 1
fi

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请确保已安装Maven"
    exit 1
fi

echo "使用内存配置: $JAVA_OPTS"
echo "正在启动后端服务..."

# 使用Maven启动Spring Boot应用
mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"

if [ $? -eq 0 ]; then
    echo "✅ 后端启动成功"
else
    echo "❌ 后端启动失败"
    exit 1
fi
