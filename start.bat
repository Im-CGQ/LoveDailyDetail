@echo off
echo ========================================
echo 记录和女朋友的每一天 - 启动脚本
echo ========================================
echo.

echo 正在启动项目...
echo.

echo 1. 启动后端服务...
cd backend
start "后端服务" cmd /k "mvn spring-boot:run"
cd ..

echo 2. 启动前端服务...
cd frontend
start "前端服务" cmd /k "npm run dev"
cd ..

echo.
echo ========================================
echo 项目启动完成！
echo ========================================
echo.
echo 前端地址: http://localhost:3000
echo 后端地址: http://localhost:8080
echo.
echo 按任意键退出...
pause > nul 