@echo off
echo 启动Docker容器...
echo.

echo 检查Docker是否安装...
docker --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker未安装或未启动
    echo 请先安装Docker Desktop并启动
    pause
    exit /b 1
)

echo ✅ Docker已安装
echo.

echo 构建并启动所有服务...
docker-compose up -d --build

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ 所有服务启动成功！
    echo.
    echo 访问地址：
    echo - 前端: http://localhost
    echo - 后端API: http://localhost:8080/api
    echo - 数据库: localhost:3306
    echo.
    echo 默认账号: admin / admin
    echo.
    echo 查看服务状态: docker-compose ps
    echo 查看日志: docker-compose logs -f
    echo 停止服务: docker-compose down
) else (
    echo.
    echo ❌ 服务启动失败！
    echo 请检查错误信息并重试
)

pause 