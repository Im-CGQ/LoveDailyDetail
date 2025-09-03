@echo off
echo ========================================
echo 电影时长字段更新工具
echo ========================================
echo.
echo 请选择操作：
echo 1. 完整重建数据库（推荐新环境）
echo 2. 只添加duration字段（推荐现有环境）
echo 3. 退出
echo.
set /p choice="请输入选择 (1-3): "

if "%choice%"=="1" goto full_rebuild
if "%choice%"=="2" goto add_fields_only
if "%choice%"=="3" goto exit
echo 无效选择，请重新运行脚本
pause
exit /b 1

:full_rebuild
echo.
echo 正在完整重建数据库...
echo 注意：这将删除所有现有数据！
echo.
set /p confirm="确认要删除所有数据吗？(y/N): "
if /i not "%confirm%"=="y" goto exit

REM 检查MySQL是否在PATH中
mysql --version >nul 2>&1
if errorlevel 1 (
    echo 错误：MySQL未安装或不在PATH中
    echo 请确保MySQL已安装并添加到系统PATH
    pause
    exit /b 1
)

echo 执行完整数据库重建...
mysql -u root -p < database/init.sql

if errorlevel 1 (
    echo 数据库重建失败！
    pause
    exit /b 1
) else (
    echo 数据库重建成功！
    echo 所有表都已创建，包括duration字段
)
goto end

:add_fields_only
echo.
echo 正在添加duration字段到现有数据库...
echo.

REM 检查MySQL是否在PATH中
mysql --version >nul 2>&1
if errorlevel 1 (
    echo 错误：MySQL未安装或不在PATH中
    echo 请确保MySQL已安装并添加到系统PATH
    pause
    exit /b 1
)

echo 执行字段添加脚本...
mysql -u root -p < database/add_duration_field_only.sql

if errorlevel 1 (
    echo 字段添加失败！
    pause
    exit /b 1
) else (
    echo 字段添加成功！
    echo duration字段已添加到movies表
)
goto end

:exit
echo 操作已取消
goto end

:end
echo.
echo 操作完成！
pause
