package com.docsy.controller;

import com.docsy.model.dto.R;
import com.docsy.model.entity.PreviewSession;
import com.docsy.service.PreviewService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * 文件操作接口 — 文件内容的获取和保存
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final PreviewService previewService;

    public FileController(PreviewService previewService) {
        this.previewService = previewService;
    }

    /**
     * 获取文件内容（预览页面加载文件时调用）
     */
    @GetMapping("/{sessionId}/content")
    public ResponseEntity<Resource> getFileContent(@PathVariable String sessionId) {
        try {
            PreviewSession session = previewService.getSession(sessionId);
            Path filePath = Path.of(session.getFilePath());
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String encodedFileName = URLEncoder.encode(session.getFileName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 保存编辑后的文件（OnlyOffice WASM 通过 postMessage 返回文件流后调用）
     */
    @PutMapping("/{sessionId}/content")
    public R<Void> saveFileContent(@PathVariable String sessionId, @RequestBody byte[] content) {
        try {
            previewService.saveFile(sessionId, content);
            return R.ok();
        } catch (IOException e) {
            return R.fail("文件保存失败: " + e.getMessage());
        }
    }
}
