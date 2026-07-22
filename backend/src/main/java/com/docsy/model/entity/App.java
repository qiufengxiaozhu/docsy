package com.docsy.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 三方应用实体
 */
@Data
@TableName("app")
public class App {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String appId;
    private String appName;
    private String appSecret;
    private String description;
    private Integer isActive;
    private Integer rateLimit;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
