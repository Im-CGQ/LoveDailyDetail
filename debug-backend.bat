@echo off
echo 后端调试脚本
echo ==============
echo.

echo 步骤1: 检查Java和Maven环境
echo --------------------------
java -version
echo.
mvn -version
echo.

echo 步骤2: 检查项目结构
echo ------------------
echo 检查关键文件是否存在...
if exist "backend\src\main\java\com\lovediary\controller\SystemConfigController.java" (
    echo ✓ SystemConfigController.java 存在
) else (
    echo ✗ SystemConfigController.java 不存在
)

if exist "backend\src\main\java\com\lovediary\service\SystemConfigService.java" (
    echo ✓ SystemConfigService.java 存在
) else (
    echo ✗ SystemConfigService.java 不存在
)

if exist "backend\src\main\java\com\lovediary\service\impl\SystemConfigServiceImpl.java" (
    echo ✓ SystemConfigServiceImpl.java 存在
) else (
    echo ✗ SystemConfigServiceImpl.java 不存在
)

if exist "backend\src\main\java\com\lovediary\repository\SystemConfigRepository.java" (
    echo ✓ SystemConfigRepository.java 存在
) else (
    echo ✗ SystemConfigRepository.java 不存在
)

if exist "backend\src\main\java\com\lovediary\entity\SystemConfig.java" (
    echo ✓ SystemConfig.java 存在
) else (
    echo ✗ SystemConfig.java 不存在
)

echo.

echo 步骤3: 尝试编译项目
echo ------------------
cd backend
echo 清理项目...
call mvn clean
echo.
echo 编译项目...
call mvn compile
echo.

echo 步骤4: 检查编译结果
echo ------------------
if errorlevel 1 (
    echo ✗ 编译失败！请检查上面的错误信息
    echo.
    echo 常见问题：
    echo 1. 缺少依赖
    echo 2. 语法错误
    echo 3. 导入包错误
    echo.
    pause
    exit /b 1
) else (
    echo ✓ 编译成功！
)

echo.

echo 步骤5: 启动应用（测试模式）
echo --------------------------
echo 注意：这将启动Spring Boot应用
echo 请等待看到 "Started LoveDiaryApplication" 消息
echo 然后按 Ctrl+C 停止应用
echo.
echo 按任意键开始启动...
pause
echo.
call mvn spring-boot:run

echo.
echo 应用已停止
pause
