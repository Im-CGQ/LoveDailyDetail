@echo off
echo 正在更新数据库，添加duration字段...
echo.

REM 检查MySQL是否在PATH中
mysql --version >nul 2>&1
if errorlevel 1 (
    echo 错误：MySQL未安装或不在PATH中
    echo 请确保MySQL已安装并添加到系统PATH
    pause
    exit /b 1
)

REM 执行数据库更新脚本
echo 执行数据库更新脚本...
mysql -u root -p < database/add_duration_seconds_field.sql

if errorlevel 1 (
    echo 数据库更新失败！
    pause
    exit /b 1
) else (
    echo 数据库更新成功！
    echo duration字段已添加到movies表
)

echo.
echo 更新完成！
pause
