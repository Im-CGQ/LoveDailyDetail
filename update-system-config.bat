@echo off
echo 正在更新系统配置数据库...

REM 检查MySQL是否运行
mysql -u root -p -e "SELECT 1;" >nul 2>&1
if errorlevel 1 (
    echo MySQL未运行，请先启动MySQL服务
    pause
    exit /b 1
)

REM 执行系统配置更新脚本
mysql -u root -p < backend/database/update_system_config.sql

if errorlevel 1 (
    echo 数据库更新失败
    pause
    exit /b 1
) else (
    echo 系统配置数据库更新成功！
    echo.
    echo 已添加以下配置：
    echo - together_date: 在一起的时间
    echo - background_music_autoplay: 背景音乐自动播放
    echo.
    echo 现在可以启动后端服务了
)

pause
