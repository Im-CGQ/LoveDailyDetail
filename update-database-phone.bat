@echo off
chcp 65001 >nul

echo 开始更新数据库，添加手机号字段...

REM 数据库连接信息
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=love_diary
set DB_USER=root
set DB_PASSWORD=123456

REM 执行SQL脚本
echo 执行数据库更新脚本...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < database\update_user_phone.sql

if %ERRORLEVEL% EQU 0 (
    echo ✅ 数据库更新成功！手机号字段已添加。
    echo 📝 注意事项：
    echo    - 新用户注册时需要提供手机号和验证码
    echo    - 现有用户可以通过用户名密码登录
    echo    - 手机号登录功能已启用，验证码为：123456
) else (
    echo ❌ 数据库更新失败！
    pause
    exit /b 1
)

echo 数据库更新完成！
pause
