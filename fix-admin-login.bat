@echo off
echo 修复admin登录问题...
echo.

echo 1. 检查MySQL服务状态...
net start | findstr MySQL
if %errorlevel% neq 0 (
    echo MySQL服务未运行，尝试启动...
    net start MySQL80
    if %errorlevel% neq 0 (
        echo 无法启动MySQL服务，请手动启动MySQL
        pause
        exit /b 1
    )
)

echo.
echo 2. 重新初始化数据库...
mysql -u root -p123456 -e "DROP DATABASE IF EXISTS love_diary;"
mysql -u root -p123456 -e "CREATE DATABASE love_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo.
echo 3. 执行数据库初始化脚本...
mysql -u root -p123456 love_diary < database/init.sql

echo.
echo 4. 验证admin用户...
mysql -u root -p123456 -e "USE love_diary; SELECT username, role FROM users WHERE username='admin';"

echo.
echo 5. 启动后端服务...
cd backend
call mvn spring-boot:run

pause
