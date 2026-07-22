package com.docsy.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 数据源初始化：确保数据目录存在并执行 schema 初始化
 * <p>
 * 根据当前数据库驱动自动选择 schema.sql（SQLite）或 schema-mysql.sql（MySQL）
 */
@Component
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    private final JdbcTemplate jdbcTemplate;
    private final DocsyProperties properties;

    @Value("${spring.datasource.driver-class-name:org.sqlite.JDBC}")
    private String driverClassName;

    public DataSourceConfig(JdbcTemplate jdbcTemplate, DocsyProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    @PostConstruct
    public void init() throws IOException {
        // 确保数据目录存在
        Path dataDir = Path.of(properties.getDataDir());
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
            log.info("创建数据目录: {}", dataDir.toAbsolutePath());
        }

        // 确保文件存储目录存在
        Path fileStore = Path.of(properties.getFileStore());
        if (!Files.exists(fileStore)) {
            Files.createDirectories(fileStore);
            log.info("创建文件存储目录: {}", fileStore.toAbsolutePath());
        }

        // 根据数据库类型选择 schema 文件
        boolean isMySQL = driverClassName.contains("mysql");
        String schemaFile = isMySQL ? "schema-mysql.sql" : "schema.sql";
        log.info("使用数据库: {}, schema 文件: {}", isMySQL ? "MySQL" : "SQLite", schemaFile);

        ClassPathResource resource = new ClassPathResource(schemaFile);
        String sql = resource.getContentAsString(StandardCharsets.UTF_8);

        // 按分号分割执行各条 SQL 语句
        for (String statement : sql.split(";")) {
            // 去除注释行后再判断是否为有效 SQL
            String cleaned = statement.lines()
                    .filter(line -> !line.trim().startsWith("--"))
                    .reduce("", (a, b) -> a + "\n" + b)
                    .trim();
            if (!cleaned.isEmpty()) {
                try {
                    jdbcTemplate.execute(cleaned);
                } catch (Exception e) {
                    // INSERT IGNORE / CREATE INDEX IF NOT EXISTS 可能因为已存在而被忽略
                    log.debug("SQL 执行提示: {}", e.getMessage());
                }
            }
        }
        log.info("数据库 schema 初始化完成");
    }
}
