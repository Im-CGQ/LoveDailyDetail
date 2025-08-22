# 使用OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 安装必要的工具
RUN apk add --no-cache curl

# 复制Maven配置文件
COPY pom.xml ./

# 复制源代码
COPY src ./src

# 复制生产环境配置（使用现有的application.yml）
# COPY ../docker/application-prod.yml ./src/main/resources/application-prod.yml

# 安装Maven（如果需要本地构建）
RUN apk add --no-cache maven

# 构建应用
RUN mvn clean package -DskipTests

# 创建非root用户
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 设置应用目录权限
RUN chown -R appuser:appgroup /app

# 切换到非root用户
USER appuser

# 暴露8080端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# 启动应用
CMD ["sh", "-c", "java $JAVA_OPTS -jar target/*.jar"]
