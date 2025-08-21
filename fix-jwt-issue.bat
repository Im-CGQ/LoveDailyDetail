@echo off
echo 修复JWT密钥长度问题...
echo.

echo 1. 停止后端服务（如果正在运行）...
taskkill /f /im java.exe 2>nul

echo.
echo 2. 清理并重新编译后端...
cd backend
call mvn clean compile

echo.
echo 3. 启动后端服务...
echo 注意：已修复JWT密钥长度问题，现在使用HS256算法
call mvn spring-boot:run

pause
