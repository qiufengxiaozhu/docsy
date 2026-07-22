package com.docsy.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 字体实体
 */
@Data
@Table("font")
public class Font {
    @Id
    private Long id;
    private String fontName;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String source;
    private String createdAt;
}
