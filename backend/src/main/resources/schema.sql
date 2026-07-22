-- Docsy 数据库 Schema（SQLite）

-- 管理员用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    username    TEXT    NOT NULL UNIQUE,
    password    TEXT    NOT NULL,             -- BCrypt 加密
    nickname    TEXT    DEFAULT '',
    is_active   INTEGER DEFAULT 1,           -- 1=启用 0=禁用
    created_at  TEXT    DEFAULT (datetime('now', 'localtime')),
    updated_at  TEXT    DEFAULT (datetime('now', 'localtime'))
);

-- 三方应用表
CREATE TABLE IF NOT EXISTS app (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    app_id      TEXT    NOT NULL UNIQUE,      -- 应用ID（三方调用时传入）
    app_name    TEXT    NOT NULL,             -- 应用名称
    app_secret  TEXT    NOT NULL,             -- 应用密钥（HMAC 签名用）
    description TEXT    DEFAULT '',           -- 应用描述
    is_active   INTEGER DEFAULT 1,           -- 1=启用 0=禁用
    rate_limit  INTEGER DEFAULT 100,         -- 每分钟请求限制
    created_at  TEXT    DEFAULT (datetime('now', 'localtime')),
    updated_at  TEXT    DEFAULT (datetime('now', 'localtime'))
);

-- 预览/编辑会话表
CREATE TABLE IF NOT EXISTS preview_session (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id   TEXT    NOT NULL UNIQUE,     -- 会话唯一标识（UUID）
    app_id       TEXT    NOT NULL,            -- 关联的三方应用
    file_name    TEXT    NOT NULL,            -- 原始文件名
    file_path    TEXT    NOT NULL,            -- 服务端存储路径
    file_type    TEXT    NOT NULL,            -- 文件扩展名（如 docx, pdf, png）
    file_size    INTEGER DEFAULT 0,          -- 文件大小（字节）
    viewer_type  TEXT    NOT NULL,            -- 渲染器类型：ONLYOFFICE/IMAGE/MEDIA/CODE/UNSUPPORTED
    mode         TEXT    NOT NULL DEFAULT 'view', -- 模式：view/edit
    user_id      TEXT    DEFAULT '',          -- 用户标识（三方传入）
    user_name    TEXT    DEFAULT '',          -- 用户名（三方传入）
    callback_url TEXT    DEFAULT '',          -- 编辑保存后回调地址
    status       TEXT    DEFAULT 'active',    -- 状态：active/expired/closed
    created_at   TEXT    DEFAULT (datetime('now', 'localtime')),
    expires_at   TEXT,                        -- 过期时间
    updated_at   TEXT    DEFAULT (datetime('now', 'localtime'))
);

-- 字体表
CREATE TABLE IF NOT EXISTS font (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    font_name   TEXT    NOT NULL,             -- 字体名称
    file_name   TEXT    NOT NULL,             -- 字体文件名
    file_path   TEXT    NOT NULL,             -- 存储路径
    file_size   INTEGER DEFAULT 0,           -- 文件大小
    source      TEXT    DEFAULT 'upload',     -- 来源：upload/system
    created_at  TEXT    DEFAULT (datetime('now', 'localtime'))
);

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    config_key  TEXT    NOT NULL UNIQUE,
    config_value TEXT   NOT NULL,
    description TEXT    DEFAULT '',
    updated_at  TEXT    DEFAULT (datetime('now', 'localtime'))
);

-- 初始数据：默认管理员（密码: admin123，BCrypt 加密）
INSERT OR IGNORE INTO sys_user (username, password, nickname)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员');

-- 初始配置
INSERT OR IGNORE INTO sys_config (config_key, config_value, description)
VALUES ('session.expire.minutes', '120', '预览会话过期时间（分钟）');
INSERT OR IGNORE INTO sys_config (config_key, config_value, description)
VALUES ('file.max.size.mb', '500', '最大上传文件大小（MB）');
INSERT OR IGNORE INTO sys_config (config_key, config_value, description)
VALUES ('file.cleanup.days', '7', '文件自动清理天数');

-- 索引
CREATE INDEX IF NOT EXISTS idx_session_app_id ON preview_session(app_id);
CREATE INDEX IF NOT EXISTS idx_session_status ON preview_session(status);
CREATE INDEX IF NOT EXISTS idx_session_expires ON preview_session(expires_at);
CREATE INDEX IF NOT EXISTS idx_app_app_id ON app(app_id);
