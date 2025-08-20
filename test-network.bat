@echo off
echo ========================================
echo 网络访问测试脚本
echo ========================================
echo.

echo 正在获取本机IP地址...
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do (
    set IP=%%a
    goto :found
)
:found
set IP=%IP: =%

echo 本机IP地址: %IP%
echo.

echo 测试端口连通性...
echo.

echo 1. 测试前端端口 (3000):
netstat -an | findstr :3000
if %errorlevel% equ 0 (
    echo ✅ 前端端口 3000 正在监听
    echo 前端访问地址: http://%IP%:3000
) else (
    echo ❌ 前端端口 3000 未启动
)
echo.

echo 2. 测试后端端口 (8080):
netstat -an | findstr :8080
if %errorlevel% equ 0 (
    echo ✅ 后端端口 8080 正在监听
    echo 后端访问地址: http://%IP%:8080
) else (
    echo ❌ 后端端口 8080 未启动
)
echo.

echo 3. 测试数据库端口 (3306):
netstat -an | findstr :3306
if %errorlevel% equ 0 (
    echo ✅ 数据库端口 3306 正在监听
) else (
    echo ❌ 数据库端口 3306 未启动
)
echo.

echo ========================================
echo 访问地址汇总:
echo ========================================
echo 本机访问:
echo   前端: http://localhost:3000
echo   后端: http://localhost:8080
echo.
echo 局域网访问:
echo   前端: http://%IP%:3000
echo   后端: http://%IP%:8080
echo.
echo 移动端访问:
echo   在手机浏览器中输入: http://%IP%:3000
echo.

echo 按任意键退出...
pause > nul 