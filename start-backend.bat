@echo off
echo 启动后端服务...
echo.

echo 1. 清理并编译项目...
call mvn clean compile
if errorlevel 1 (
    echo 编译失败！请检查错误信息。
    pause
    exit /b 1
)

echo.
echo 2. 启动Spring Boot应用...
echo 请等待应用启动完成，然后按Ctrl+C停止服务
echo.
call mvn spring-boot:run

echo.
echo 后端服务已停止。
pause
