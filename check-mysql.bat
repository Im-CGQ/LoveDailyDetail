@echo off
chcp 65001 >nul
echo ========================================
echo         MySQL服务检查工具
echo ========================================
echo.

echo 正在检查MySQL服务状态...

sc query mysql >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo ✅ MySQL服务已安装
    sc query mysql | find "RUNNING"
    if %ERRORLEVEL% EQU 0 (
        echo ✅ MySQL服务正在运行
    ) else (
        echo ❌ MySQL服务未运行
        echo.
        set /p START_MYSQL=是否启动MySQL服务？(y/n): 
        if /i "%START_MYSQL%"=="y" (
            echo 正在启动MySQL服务...
            net start mysql
            if %ERRORLEVEL% EQU 0 (
                echo ✅ MySQL服务启动成功
            ) else (
                echo ❌ MySQL服务启动失败，请手动启动
            )
        )
    )
) else (
    echo ❌ MySQL服务未安装或未找到
    echo 请先安装MySQL服务
)

echo.
echo 正在测试MySQL连接...

mysql -uroot -p123456 -e "SELECT 1;" >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo ✅ MySQL连接测试成功
    echo 默认用户名: root
    echo 默认密码: 123456
) else (
    echo ❌ MySQL连接测试失败
    echo 请检查用户名和密码
)

echo.
pause
