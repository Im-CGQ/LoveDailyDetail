@echo off
echo 启动后端服务...
echo.

cd backend
echo 正在编译后端项目...
call mvn clean compile

echo.
echo 启动Spring Boot应用...
call mvn spring-boot:run

pause 