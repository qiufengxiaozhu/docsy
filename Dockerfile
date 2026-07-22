# Docsy - 多阶段构建

# ===== 阶段1: 前端构建 =====
FROM node:20-alpine AS frontend-build
WORKDIR /build
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm install --registry=https://registry.npmmirror.com
COPY frontend/ ./
RUN npm run build

# ===== 阶段2: 后端构建 =====
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /build
# 先复制 pom 以利用 Docker 缓存
COPY pom.xml ./
COPY backend/pom.xml backend/
RUN mvn dependency:go-offline -B -pl backend 2>/dev/null || true
# 复制源码并构建
COPY backend/ backend/
RUN mvn package -DskipTests -pl backend -f pom.xml

# ===== 阶段3: 运行时 =====
FROM eclipse-temurin:21-jre-jammy

LABEL maintainer="qiufengxiaozhu"
LABEL description="Docsy - 文档预览与编辑服务"

WORKDIR /app

# 复制后端 JAR
COPY --from=backend-build /build/backend/target/docsy.jar ./docsy.jar

# 复制前端产物到静态资源目录
COPY --from=frontend-build /build/dist/ ./static/admin/

# 复制 OnlyOffice WASM 静态资源（文件系统方式加载，不打入 JAR）
COPY onlyoffice/ ./onlyoffice/

# 复制 OnlyOffice 定制化资源（优先级高于原始资源）
COPY onlyoffice-custom/ ./onlyoffice-custom/

# 数据目录
RUN mkdir -p /app/data/files

# 暴露端口
EXPOSE 8080

# 数据卷
VOLUME ["/app/data"]

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动
ENTRYPOINT ["java", "-jar", "docsy.jar", \
    "--docsy.data-dir=/app/data", \
    "--docsy.file-store=/app/data/files", \
    "--docsy.onlyoffice-path=file:/app/onlyoffice/", \
    "--docsy.onlyoffice-custom-path=file:/app/onlyoffice-custom/"]
