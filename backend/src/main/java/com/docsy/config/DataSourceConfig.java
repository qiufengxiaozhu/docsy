package com.docsy.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 数据源初始化：确保数据目录存在并执行 schema.sql
 */
@Component
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    private final JdbcTemplate jdbcTemplate;
    private final DocsyProperties properties;

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

        // 执行 schema.sql 初始化表结构
        ClassPathResource resource = new ClassPathResource("schema.sql");
        String sql = resource.getContentAsString(StandardCharsets.UTF_8);
        // SQLite 支持分号分割执行多条语句
        for (String statement : sql.split(";")) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                try {
                    jdbcTemplate.execute(trimmed);
                } catch (Exception e) {
                    // INSERT OR IGNORE 可能因为已存在而被忽略，这是正常的
                    log.debug("SQL 执行提示: {}", e.getMessage());
                }
            }
        }
        log.info("数据库 schema 初始化完成");
    }
}
