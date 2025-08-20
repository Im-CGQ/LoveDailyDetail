@echo off
echo 初始化数据库...

mysql -u root -p123456 < database/init.sql

echo 数据库初始化完成！
pause 