# 阶段1：构建应用
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制pom.xml并下载依赖（利用Docker缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 阶段2：运行应用
FROM eclipse-temurin-17-jre-alpine

WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder /app/target/aitravelplanner-backend-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置环境变量
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 运行应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]