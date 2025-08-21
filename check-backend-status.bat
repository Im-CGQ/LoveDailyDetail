@echo off
echo 检查后端服务状态...
echo.

echo 1. 检查后端服务是否运行...
curl -X GET "http://localhost:8080/api/test/health" -H "Content-Type: application/json"
echo.
echo.

echo 2. 检查系统配置测试端点...
curl -X GET "http://localhost:8080/api/test/system-config-test" -H "Content-Type: application/json"
echo.
echo.

echo 3. 测试系统配置API...
curl -X GET "http://localhost:8080/api/system-config/together-date" -H "Content-Type: application/json"
echo.
echo.

echo 检查完成！
echo.
echo 如果步骤1和2成功但步骤3失败，说明：
echo - Spring Boot应用正常运行
echo - 但SystemConfig相关的类可能有编译错误或没有被正确加载
echo.
echo 请检查：
echo 1. 后端服务是否已启动 (运行 start-backend.bat)
echo 2. 数据库是否已更新 (运行 update-system-config.bat)
echo 3. 控制台是否有编译错误
pause
