FROM openjdk:8-jdk-alpine

WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .
COPY src ./src

# 安装Maven并构建项目
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests && \
    apk del maven

# 暴露端口
EXPOSE 8080

# 运行应用
CMD ["java", "-jar", "target/love-diary-backend-1.0.0.jar"] 