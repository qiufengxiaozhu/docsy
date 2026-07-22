-- Docsy 数据库 Schema（MySQL）

-- 管理员用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64)  NOT NULL UNIQUE,
    password    VARCHAR(128) NOT NULL             COMMENT 'BCrypt 加密',
    nickname    VARCHAR(64)  DEFAULT '',
    is_active   TINYINT      DEFAULT 1            COMMENT '1=启用 0=禁用',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 三方应用表
CREATE TABLE IF NOT EXISTS app (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    app_id      VARCHAR(32)  NOT NULL UNIQUE      COMMENT '应用ID（三方调用时传入）',
    app_name    VARCHAR(128) NOT NULL              COMMENT '应用名称',
    app_secret  VARCHAR(128) NOT NULL              COMMENT '应用密钥（HMAC 签名用）',
    description VARCHAR(256) DEFAULT ''            COMMENT '应用描述',
    is_active   TINYINT      DEFAULT 1             COMMENT '1=启用 0=禁用',
    rate_limit  INT          DEFAULT 100           COMMENT '每分钟请求限制',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 预览/编辑会话表
CREATE TABLE IF NOT EXISTS preview_session (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    session_id   VARCHAR(64)  NOT NULL UNIQUE      COMMENT '会话唯一标识（UUID）',
    app_id       VARCHAR(32)  NOT NULL              COMMENT '关联的三方应用',
    file_name    VARCHAR(256) NOT NULL              COMMENT '原始文件名',
    file_path    VARCHAR(512) NOT NULL              COMMENT '服务端存储路径',
    file_type    VARCHAR(16)  NOT NULL              COMMENT '文件扩展名（如 docx, pdf, png）',
    file_size    BIGINT       DEFAULT 0             COMMENT '文件大小（字节）',
    viewer_type  VARCHAR(32)  NOT NULL              COMMENT '渲染器类型：ONLYOFFICE/IMAGE/MEDIA/CODE/UNSUPPORTED',
    mode         VARCHAR(16)  NOT NULL DEFAULT 'view' COMMENT '模式：view/edit',
    user_id      VARCHAR(64)  DEFAULT ''            COMMENT '用户标识（三方传入）',
    user_name    VARCHAR(64)  DEFAULT ''            COMMENT '用户名（三方传入）',
    callback_url VARCHAR(512) DEFAULT ''            COMMENT '编辑保存后回调地址',
    status       VARCHAR(16)  DEFAULT 'active'      COMMENT '状态：active/expired/closed',
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    expires_at   DATETIME                           COMMENT '过期时间',
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_session_app_id (app_id),
    INDEX idx_session_status (status),
    INDEX idx_session_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 字体表
CREATE TABLE IF NOT EXISTS font (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    font_name   VARCHAR(128) NOT NULL              COMMENT '字体名称',
    file_name   VARCHAR(128) NOT NULL              COMMENT '字体文件名',
    file_path   VARCHAR(512) NOT NULL              COMMENT '存储路径',
    file_size   BIGINT       DEFAULT 0             COMMENT '文件大小',
    source      VARCHAR(16)  DEFAULT 'upload'      COMMENT '来源：upload/system',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    config_key   VARCHAR(64)  NOT NULL UNIQUE,
    config_value VARCHAR(512) NOT NULL,
    description  VARCHAR(256) DEFAULT '',
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 初始数据：默认管理员（密码: admin123，BCrypt 加密）
INSERT IGNORE INTO sys_user (username, password, nickname)
VALUES ('admin', '$2a$10$dcsMT15A0ZMIIKk/LtkriuRv.gs0gMYwHa7pW1Iq23nMQZZCsodta', '管理员');

-- 初始配置
INSERT IGNORE INTO sys_config (config_key, config_value, description)
VALUES ('session.expire.minutes', '120', '预览会话过期时间（分钟）');
INSERT IGNORE INTO sys_config (config_key, config_value, description)
VALUES ('file.max.size.mb', '500', '最大上传文件大小（MB）');
INSERT IGNORE INTO sys_config (config_key, config_value, description)
VALUES ('file.cleanup.days', '7', '文件自动清理天数');

-- app 表索引（单独创建，因为 CREATE TABLE 中已包含 preview_session 索引）
CREATE INDEX IF NOT EXISTS idx_app_app_id ON app(app_id);
