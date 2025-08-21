@echo off
echo 测试系统配置功能...
echo.

echo 1. 测试获取在一起时间...
curl -X GET "http://localhost:8080/api/system-config/together-date" -H "Content-Type: application/json"
echo.
echo.

echo 2. 测试获取背景音乐自动播放配置...
curl -X GET "http://localhost:8080/api/system-config/background-music-autoplay" -H "Content-Type: application/json"
echo.
echo.

echo 3. 测试获取所有配置...
curl -X GET "http://localhost:8080/api/system-config/all" -H "Content-Type: application/json"
echo.
echo.

echo 测试完成！
pause
