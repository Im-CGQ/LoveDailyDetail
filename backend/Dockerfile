FROM java:8
COPY  ./target/love-diary-backend-1.0.0.jar  /love-diary-backend-1.0.0.jar
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["java", "-jar","/love-diary-backend-1.0.0.jar"] 
