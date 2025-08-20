@echo off
chcp 65001 >nul
echo ========================================
echo           数据库初始化工具
echo ========================================
echo.

echo 请确保MySQL服务已启动，并且已安装MySQL客户端
echo.

set /p MYSQL_USER=请输入MySQL用户名 (默认: root): 
if "%MYSQL_USER%"=="" set MYSQL_USER=root

set /p MYSQL_PASS=请输入MySQL密码: 

echo.
echo 正在执行数据库初始化脚本...

mysql -u%MYSQL_USER% -p%MYSQL_PASS% --default-character-set=utf8mb4 < backend/database/init.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ 数据库初始化成功！
    echo.
    echo 数据库信息：
    echo - 数据库名: love_diary
    echo - 默认管理员账号: admin / admin
    echo.
    echo 现在可以启动后端服务了
) else (
    echo.
    echo ❌ 数据库初始化失败！
    echo.
    echo 可能的原因：
    echo 1. MySQL服务未启动
    echo 2. 用户名或密码错误
    echo 3. 没有创建数据库的权限
    echo 4. MySQL客户端未安装或不在PATH中
    echo.
    echo 解决方案：
    echo 1. 启动MySQL服务: net start mysql
    echo 2. 检查用户名密码
    echo 3. 使用管理员权限运行此脚本
    echo 4. 确保MySQL客户端已安装
)

echo.
pause 