@echo off
chcp 65001 >nul

echo 开始更新数据库，添加用户头像字段...

REM 数据库配置
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=love_diary
set DB_USER=root
set DB_PASSWORD=

REM 检查MySQL是否运行
mysqladmin ping -h%DB_HOST% -P%DB_PORT% -u%DB_USER% --silent >nul 2>&1
if errorlevel 1 (
    echo 错误: 无法连接到MySQL数据库，请确保MySQL服务正在运行
    pause
    exit /b 1
)

REM 执行数据库更新
echo 执行数据库更新脚本...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < database\update_user_avatar_simple.sql

if errorlevel 1 (
    echo ❌ 数据库更新失败
    pause
    exit /b 1
) else (
    echo ✅ 数据库更新成功！用户头像字段已添加
)

echo 数据库更新完成！
pause
