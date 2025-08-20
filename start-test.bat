@echo off
echo 启动测试...

cd backend

echo 清理并编译项目...
call mvn clean compile

echo 启动应用...
call mvn spring-boot:run

pause 