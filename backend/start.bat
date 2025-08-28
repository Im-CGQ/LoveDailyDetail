@echo off
chcp 65001 >nul

echo 启动Love Diary后端服务...

REM 设置JVM内存参数
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m

REM 检查Java是否安装
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java环境，请确保已安装Java 11或更高版本
    pause
    exit /b 1
)

REM 检查Maven是否安装
mvn -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Maven环境，请确保已安装Maven
    pause
    exit /b 1
)

echo 使用内存配置: %JAVA_OPTS%
echo 正在启动后端服务...

REM 使用Maven启动Spring Boot应用
mvn spring-boot:run -Dspring-boot.run.jvmArguments="%JAVA_OPTS%"

if errorlevel 1 (
    echo ❌ 后端启动失败
    pause
    exit /b 1
) else (
    echo ✅ 后端启动成功
)
