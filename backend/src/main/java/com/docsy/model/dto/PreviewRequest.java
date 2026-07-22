package com.docsy.model.dto;

import lombok.Data;

/**
 * 创建预览会话请求（三方应用调用）
 */
@Data
public class PreviewRequest {
    /** 远程文件 URL（与 file 二选一） */
    private String fileUrl;

    /** 文件名（当使用 fileUrl 时必填） */
    private String fileName;

    /** 模式：view 或 edit */
    private String mode = "view";

    /** 用户标识 */
    private String userId;

    /** 用户名 */
    private String userName;

    /** 编辑保存后回调 URL */
    private String callbackUrl;
}
