package com.docsy.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 三方应用实体
 */
@Data
@Table("app")
public class App {
    @Id
    private Long id;
    private String appId;
    private String appName;
    private String appSecret;
    private String description;
    private Integer isActive;
    private Integer rateLimit;
    private String createdAt;
    private String updatedAt;
}
