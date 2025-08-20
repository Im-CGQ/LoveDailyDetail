@echo off
echo ========================================
echo    Love Diary Admin 后台管理启动脚本
echo ========================================
echo.

echo 正在检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java环境，请先安装Java 8或更高版本
    pause
    exit /b 1
)
echo [成功] Java环境检测正常
echo.

echo 正在检查Node.js环境...
node --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Node.js环境，请先安装Node.js
    pause
    exit /b 1
)
echo [成功] Node.js环境检测正常
echo.

echo 正在检查MySQL服务...
net start | findstr "MySQL" >nul 2>&1
if errorlevel 1 (
    echo [警告] MySQL服务未启动，尝试启动MySQL...
    net start MySQL80 >nul 2>&1
    if errorlevel 1 (
        echo [错误] 无法启动MySQL服务，请手动启动MySQL
        pause
        exit /b 1
    )
)
echo [成功] MySQL服务运行正常
echo.

echo 正在检查数据库连接...
cd backend
echo 正在初始化数据库...
call mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev" >nul 2>&1 &
set BACKEND_PID=%ERRORLEVEL%
cd ..

echo 等待后端服务启动...
timeout /t 10 /nobreak >nul

echo 正在启动前端服务...
cd frontend
start "Love Diary Frontend" cmd /k "npm run dev"
cd ..

echo.
echo ========================================
echo    启动完成！
echo ========================================
echo.
echo 前端地址: http://localhost:3000
echo 后台管理: http://localhost:3000/admin
echo API测试: http://localhost:3000/test-admin-api.html
echo.
echo 后端API: http://localhost:8080
echo.
echo 按任意键打开后台管理页面...
pause >nul

start http://localhost:3000/admin

echo.
echo 提示：
echo 1. 如果页面无法访问，请等待几秒钟后刷新
echo 2. 首次访问需要登录，可以使用测试账号
echo 3. 如果遇到问题，请查看控制台日志
echo 4. 测试API功能请访问: http://localhost:3000/test-admin-api.html
echo.
echo 按任意键退出...
pause >nul 