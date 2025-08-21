@echo off
echo 快速测试后端状态...
echo.

echo 1. 检查8080端口是否被占用...
netstat -ano | findstr :8080
echo.

echo 2. 测试基本连接...
curl -X GET "http://localhost:8080/api/simple/ping" --connect-timeout 5
echo.
echo.

echo 3. 如果上面失败，说明后端服务未启动
echo 请运行: start-backend.bat
echo.

pause
