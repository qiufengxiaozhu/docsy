package com.docsy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Docsy 自定义配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "docsy")
public class DocsyProperties {

    /** 数据目录 */
    private String dataDir = "./data";

    /** 文件存储路径 */
    private String fileStore = "./data/files";

    /** JWT 配置 */
    private Jwt jwt = new Jwt();

    /** 三方应用签名有效期（秒） */
    private int signExpireSeconds = 300;

    /** 预览会话过期时间（分钟） */
    private int sessionExpireMinutes = 120;

    /** OnlyOffice WASM 静态资源路径 */
    private String onlyofficePath = "/static/onlyoffice";

    @Data
    public static class Jwt {
        /** JWT 签名密钥 */
        private String secret = "docsy-default-jwt-secret-please-change-in-production-2024";
        /** JWT 有效期（毫秒） */
        private long expiration = 86400000L;
    }
}
