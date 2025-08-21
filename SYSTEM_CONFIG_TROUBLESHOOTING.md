# 系统配置功能故障排除指南

## 问题：API返回404错误

### 症状
- 访问 `http://localhost:8080/api/system-config/together-date` 返回404
- 其他系统配置API也返回404

### 可能的原因和解决方案

#### 1. 后端服务未启动
**检查方法：**
```bash
# 检查8080端口是否被占用
netstat -ano | findstr :8080
```

**解决方案：**
```bash
# 启动后端服务
cd backend
mvn spring-boot:run
```

#### 2. 编译错误
**检查方法：**
- 查看控制台输出是否有编译错误
- 检查是否有 "Error" 或 "Exception" 消息

**解决方案：**
```bash
# 清理并重新编译
cd backend
mvn clean compile
```

#### 3. 数据库未更新
**检查方法：**
- 检查MySQL服务是否运行
- 检查 `system_configs` 表是否存在

**解决方案：**
```bash
# 运行数据库更新脚本
update-system-config.bat
```

#### 4. Spring Boot组件扫描问题
**检查方法：**
- 确保所有文件都在正确的包路径下
- 检查是否有包名错误

**解决方案：**
- 确保文件路径正确：
  - `SystemConfigController.java` → `backend/src/main/java/com/lovediary/controller/`
  - `SystemConfigService.java` → `backend/src/main/java/com/lovediary/service/`
  - `SystemConfigServiceImpl.java` → `backend/src/main/java/com/lovediary/service/impl/`
  - `SystemConfigRepository.java` → `backend/src/main/java/com/lovediary/repository/`
  - `SystemConfig.java` → `backend/src/main/java/com/lovediary/entity/`

#### 5. 依赖注入问题
**检查方法：**
- 查看启动日志是否有依赖注入错误
- 检查 `@Autowired` 注解是否正确

**解决方案：**
- 确保所有类都有正确的注解：
  - Controller: `@RestController`
  - Service: `@Service`
  - Repository: `@Repository`
  - Entity: `@Entity`

## 诊断步骤

### 步骤1：运行诊断脚本
```bash
diagnose-system-config.bat
```

### 步骤2：检查测试端点
```bash
# 测试基本连接
curl http://localhost:8080/api/test/health

# 测试系统配置
curl http://localhost:8080/api/system-config/together-date
```

### 步骤3：检查日志
查看Spring Boot启动日志，寻找：
- "Started LoveDiaryApplication"
- "Mapped" 消息（显示API端点映射）
- 任何错误或异常

### 步骤4：验证数据库
```sql
-- 连接到MySQL并检查表
USE love_diary;
SHOW TABLES;
SELECT * FROM system_configs;
```

## 常见错误和解决方案

### 错误1：找不到符号 @Id
**原因：** 导入包错误
**解决方案：** 使用 `javax.persistence.*` 而不是 `jakarta.persistence.*`

### 错误2：Bean创建失败
**原因：** 依赖注入问题
**解决方案：** 检查所有 `@Autowired` 注解和对应的Bean定义

### 错误3：表不存在
**原因：** 数据库脚本未执行
**解决方案：** 运行 `update-system-config.bat`

### 错误4：端口被占用
**原因：** 8080端口已被其他应用占用
**解决方案：** 
```bash
# 查找占用端口的进程
netstat -ano | findstr :8080
# 终止进程或修改application.properties中的端口
```

## 验证清单

- [ ] 后端服务正在运行
- [ ] 没有编译错误
- [ ] 数据库已更新
- [ ] 所有文件都在正确的包路径下
- [ ] 所有类都有正确的注解
- [ ] 测试端点可以访问
- [ ] 系统配置API可以访问

## 联系支持

如果按照以上步骤仍然无法解决问题，请：
1. 运行 `diagnose-system-config.bat` 并保存输出
2. 检查控制台日志
3. 提供错误信息和诊断结果
