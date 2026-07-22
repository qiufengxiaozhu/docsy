package com.docsy.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字体实体
 */
@Data
@TableName("font")
public class Font {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fontName;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String source;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
