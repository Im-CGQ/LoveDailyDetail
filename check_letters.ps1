Write-Host "正在检查信件表数据..." -ForegroundColor Green
Write-Host ""

# 连接到MySQL并执行查询
mysql -u root -p123456 -e "source backend/database/check_letters.sql"

Write-Host ""
Write-Host "检查完成！" -ForegroundColor Green
Read-Host "按任意键继续"
