@echo off
echo 测试登录...

echo 等待服务启动...
timeout /t 5 /nobreak >nul

echo 测试 admin/admin 登录...
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin\"}"

echo.
echo 测试完成！
pause 