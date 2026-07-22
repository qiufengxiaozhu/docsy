package com.docsy.controller;

import com.docsy.model.dto.PreviewRequest;
import com.docsy.model.dto.R;
import com.docsy.model.entity.App;
import com.docsy.service.PreviewService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 预览 API — 三方应用调用（HMAC 签名验证）
 */
@RestController
@RequestMapping("/api/preview")
public class PreviewController {

    private final PreviewService previewService;

    public PreviewController(PreviewService previewService) {
        this.previewService = previewService;
    }

    /**
     * 创建预览/编辑会话（文件上传方式）
     * 三方应用需携带 HMAC 签名 Headers: X-App-Id, X-Timestamp, X-Nonce, X-Sign
     */
    @PostMapping("/create")
    public R<Map<String, Object>> createPreview(
            @RequestHeader("X-App-Id") String appId,
            @RequestHeader("X-Timestamp") String timestamp,
            @RequestHeader("X-Nonce") String nonce,
            @RequestHeader("X-Sign") String sign,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "request", required = false) PreviewRequest request) {
        try {
            // 验证签名
            App app = previewService.verifyApp(appId, timestamp, nonce, sign);

            if (request == null) {
                request = new PreviewRequest();
            }

            Map<String, Object> result;
            if (file != null && !file.isEmpty()) {
                // 文件上传方式
                result = previewService.createSession(app, file, request);
            } else if (request.getFileUrl() != null && !request.getFileUrl().isBlank()) {
                // URL 方式
                result = previewService.createSessionFromUrl(app, request);
            } else {
                return R.fail(400, "请提供文件（file）或远程文件URL（fileUrl）");
            }

            return R.ok(result);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 查询会话状态
     */
    @GetMapping("/session/{sessionId}")
    public R<Map<String, Object>> getSession(
            @RequestHeader("X-App-Id") String appId,
            @RequestHeader("X-Timestamp") String timestamp,
            @RequestHeader("X-Nonce") String nonce,
            @RequestHeader("X-Sign") String sign,
            @PathVariable String sessionId) {
        try {
            previewService.verifyApp(appId, timestamp, nonce, sign);
            var session = previewService.getSession(sessionId);

            Map<String, Object> result = Map.of(
                    "sessionId", session.getSessionId(),
                    "fileName", session.getFileName(),
                    "fileType", session.getFileType(),
                    "mode", session.getMode(),
                    "status", session.getStatus(),
                    "createdAt", session.getCreatedAt(),
                    "expiresAt", session.getExpiresAt()
            );
            return R.ok(result);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }
}
