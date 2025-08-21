@echo off
echo 正在检查信件表数据...
echo.

REM 连接到MySQL并执行查询
mysql -u root -p123456 -e "source backend/database/check_letters.sql"

echo.
echo 检查完成！
pause
