package com.docsy.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 预览/编辑会话实体
 */
@Data
@Table("preview_session")
public class PreviewSession {
    @Id
    private Long id;
    private String sessionId;
    private String appId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String viewerType;
    private String mode;
    private String userId;
    private String userName;
    private String callbackUrl;
    private String status;
    private String createdAt;
    private String expiresAt;
    private String updatedAt;
}
