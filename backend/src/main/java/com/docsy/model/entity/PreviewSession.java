package com.docsy.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预览/编辑会话实体
 */
@Data
@TableName("preview_session")
public class PreviewSession {
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
