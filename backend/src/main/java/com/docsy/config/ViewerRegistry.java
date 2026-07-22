package com.docsy.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 渲染器注册表 — 文件格式到预览渲染器类型的映射
 * 支持通过配置或运行时动态扩展
 */
@Component
public class ViewerRegistry {

    /**
     * 渲染器类型枚举
     */
    public enum ViewerType {
        /** OnlyOffice WASM — Office 文档 + PDF */
        ONLYOFFICE("viewer-onlyoffice"),
        /** 图片查看器 */
        IMAGE("viewer-image"),
        /** 音视频播放器 */
        MEDIA("viewer-media"),
        /** 代码/文本查看器（Markdown、TXT、XML、HTML、JSON 等） */
        CODE("viewer-code"),
        /** 不支持的格式（提供下载） */
        UNSUPPORTED("viewer-unsupported");

        /** 对应的 Thymeleaf 模板名称 */
        private final String templateName;

        ViewerType(String templateName) {
            this.templateName = templateName;
        }

        public String getTemplateName() {
            return templateName;
        }
    }

    /** 格式-渲染器映射表 */
    private final Map<String, ViewerType> registry = new ConcurrentHashMap<>();

    public ViewerRegistry() {
        // OnlyOffice WASM 覆盖：Office + PDF
        registerAll(ViewerType.ONLYOFFICE,
                "docx", "doc", "docm", "dotx", "dotm",
                "xlsx", "xls", "xlsm", "xltx", "xltm", "csv",
                "pptx", "ppt", "pptm", "potx", "potm",
                "pdf",
                "odt", "ods", "odp", "odg",
                "rtf", "epub"
        );

        // 图片
        registerAll(ViewerType.IMAGE,
                "png", "jpg", "jpeg", "gif", "bmp", "svg", "webp",
                "ico", "tiff", "tif"
        );

        // 音视频
        registerAll(ViewerType.MEDIA,
                "mp4", "webm", "mkv", "avi", "mov", "flv",
                "mp3", "wav", "ogg", "flac", "aac", "wma"
        );

        // 代码/文本
        registerAll(ViewerType.CODE,
                "md", "txt", "xml", "html", "htm", "json",
                "yaml", "yml", "toml", "ini", "cfg", "conf",
                "java", "py", "js", "ts", "css", "scss", "less",
                "sql", "sh", "bat", "ps1", "log",
                "c", "cpp", "h", "hpp", "go", "rs", "rb", "php"
        );
    }

    /**
     * 注册单个格式
     */
    public void register(String extension, ViewerType type) {
        registry.put(extension.toLowerCase(), type);
    }

    /**
     * 批量注册格式
     */
    public void registerAll(ViewerType type, String... extensions) {
        for (String ext : extensions) {
            registry.put(ext.toLowerCase(), type);
        }
    }

    /**
     * 根据文件扩展名获取渲染器类型
     */
    public ViewerType resolve(String extension) {
        if (extension == null || extension.isBlank()) {
            return ViewerType.UNSUPPORTED;
        }
        return registry.getOrDefault(extension.toLowerCase().replace(".", ""), ViewerType.UNSUPPORTED);
    }

    /**
     * 从文件名解析渲染器类型
     */
    public ViewerType resolveByFileName(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ViewerType.UNSUPPORTED;
        }
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
        return resolve(ext);
    }

    /**
     * 获取所有注册的格式
     */
    public Map<String, ViewerType> getAll() {
        return Map.copyOf(registry);
    }

    /**
     * 判断是否支持某格式
     */
    public boolean isSupported(String extension) {
        return resolve(extension) != ViewerType.UNSUPPORTED;
    }

    /**
     * 判断是否支持编辑
     */
    public boolean isEditable(String extension) {
        ViewerType type = resolve(extension);
        return type == ViewerType.ONLYOFFICE;
    }
}
