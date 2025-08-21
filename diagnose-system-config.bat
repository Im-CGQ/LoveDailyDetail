@echo off
echo 系统配置功能诊断...
echo ========================
echo.

echo 步骤1: 检查后端服务状态
echo ------------------------
curl -X GET "http://localhost:8080/api/test/health" -H "Content-Type: application/json"
echo.
echo.

echo 步骤2: 检查数据库连接
echo ----------------------
echo 请确保MySQL服务正在运行，并且已执行数据库更新脚本
echo 运行: update-system-config.bat
echo.

echo 步骤3: 检查系统配置API
echo ----------------------
curl -X GET "http://localhost:8080/api/system-config/together-date" -H "Content-Type: application/json"
echo.
echo.

echo 步骤4: 检查所有系统配置端点
echo ----------------------------
echo 测试获取所有配置...
curl -X GET "http://localhost:8080/api/system-config/all" -H "Content-Type: application/json"
echo.
echo.

echo 步骤5: 检查全局配置
echo ------------------
curl -X GET "http://localhost:8080/api/system-config/global" -H "Content-Type: application/json"
echo.
echo.

echo 诊断完成！
echo ========================
echo.
echo 如果看到404错误，请按以下步骤操作：
echo.
echo 1. 确保后端服务已启动:
echo    - 运行 start-backend.bat
echo    - 等待看到 "Started LoveDiaryApplication" 消息
echo.
echo 2. 确保数据库已更新:
echo    - 运行 update-system-config.bat
echo    - 确保没有错误信息
echo.
echo 3. 检查编译错误:
echo    - 查看控制台输出
echo    - 确保没有 "Error" 或 "Exception" 消息
echo.
echo 4. 如果问题仍然存在:
echo    - 检查 SystemConfigController.java 文件是否存在
echo    - 检查 SystemConfigService.java 文件是否存在
echo    - 检查 SystemConfigRepository.java 文件是否存在
echo.
pause
