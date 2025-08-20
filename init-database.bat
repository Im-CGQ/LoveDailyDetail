@echo off
echo 初始化数据库...
echo.

echo 请确保MySQL服务已启动，并且已安装MySQL客户端
echo.

set /p MYSQL_USER=请输入MySQL用户名 (默认: root): 
if "%MYSQL_USER%"=="" set MYSQL_USER=root

set /p MYSQL_PASS=请输入MySQL密码: 

echo.
echo 正在执行数据库初始化脚本...

mysql -u%MYSQL_USER% -p%MYSQL_PASS% < backend/database/init.sql

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
    echo 请检查：
    echo 1. MySQL服务是否启动
    echo 2. 用户名密码是否正确
    echo 3. 是否有创建数据库的权限
)

pause 