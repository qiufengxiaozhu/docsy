package com.docsy.service;

import com.docsy.config.DocsyProperties;
import com.docsy.config.ViewerRegistry;
import com.docsy.model.dto.PreviewRequest;
import com.docsy.model.entity.App;
import com.docsy.model.entity.PreviewSession;
import com.docsy.repository.AppRepository;
import com.docsy.repository.PreviewSessionRepository;
import com.docsy.security.HmacVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 预览会话服务 — 核心业务逻辑
 */
@Service
public class PreviewService {

    private static final Logger log = LoggerFactory.getLogger(PreviewService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PreviewSessionRepository sessionRepository;
    private final AppRepository appRepository;
    private final ViewerRegistry viewerRegistry;
    private final HmacVerifier hmacVerifier;
    private final DocsyProperties properties;
    private final HttpClient httpClient;

    public PreviewService(PreviewSessionRepository sessionRepository,
                          AppRepository appRepository,
                          ViewerRegistry viewerRegistry,
                          HmacVerifier hmacVerifier,
                          DocsyProperties properties) {
        this.sessionRepository = sessionRepository;
        this.appRepository = appRepository;
        this.viewerRegistry = viewerRegistry;
        this.hmacVerifier = hmacVerifier;
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    /**
     * 验证三方应用签名
     */
    public App verifyApp(String appId, String timestamp, String nonce, String sign) {
        App app = appRepository.findByAppId(appId)
                .orElseThrow(() -> new RuntimeException("应用不存在: " + appId));

        if (app.getIsActive() != 1) {
            throw new RuntimeException("应用已被禁用: " + appId);
        }

        if (!hmacVerifier.verify(app.getAppSecret(), timestamp, nonce, sign)) {
            throw new RuntimeException("签名验证失败");
        }

        return app;
    }

    /**
     * 创建预览会话（通过文件上传）
     */
    public Map<String, Object> createSession(App app, MultipartFile file, PreviewRequest request) throws IOException {
        String fileName = file.getOriginalFilename();
        String sessionId = UUID.randomUUID().toString().replace("-", "");

        // 存储文件
        Path sessionDir = Path.of(properties.getFileStore(), sessionId);
        Files.createDirectories(sessionDir);
        Path filePath = sessionDir.resolve(fileName);
        file.transferTo(filePath.toFile());

        return buildSession(app, sessionId, fileName, filePath.toString(), file.getSize(), request);
    }

    /**
     * 创建预览会话（通过远程 URL）
     */
    public Map<String, Object> createSessionFromUrl(App app, PreviewRequest request) throws IOException, InterruptedException {
        String fileUrl = request.getFileUrl();
        String fileName = request.getFileName();
        if (fileName == null || fileName.isBlank()) {
            // 从 URL 提取文件名
            String path = URI.create(fileUrl).getPath();
            fileName = path.substring(path.lastIndexOf('/') + 1);
        }

        String sessionId = UUID.randomUUID().toString().replace("-", "");

        // 下载文件
        Path sessionDir = Path.of(properties.getFileStore(), sessionId);
        Files.createDirectories(sessionDir);
        Path filePath = sessionDir.resolve(fileName);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(fileUrl))
                .GET()
                .build();
        HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new RuntimeException("文件下载失败, HTTP " + response.statusCode());
        }

        try (InputStream is = response.body()) {
            Files.copy(is, filePath);
        }

        long fileSize = Files.size(filePath);
        return buildSession(app, sessionId, fileName, filePath.toString(), fileSize, request);
    }

    private Map<String, Object> buildSession(App app, String sessionId, String fileName,
                                              String filePath, long fileSize, PreviewRequest request) {
        String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.') + 1) : "";
        ViewerRegistry.ViewerType viewerType = viewerRegistry.resolve(ext);
        String mode = request.getMode() != null ? request.getMode() : "view";

        // 如果不支持编辑，强制为 view
        if ("edit".equals(mode) && !viewerRegistry.isEditable(ext)) {
            mode = "view";
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(properties.getSessionExpireMinutes());

        PreviewSession session = new PreviewSession();
        session.setSessionId(sessionId);
        session.setAppId(app.getAppId());
        session.setFileName(fileName);
        session.setFilePath(filePath);
        session.setFileType(ext);
        session.setFileSize(fileSize);
        session.setViewerType(viewerType.name());
        session.setMode(mode);
        session.setUserId(request.getUserId() != null ? request.getUserId() : "");
        session.setUserName(request.getUserName() != null ? request.getUserName() : "");
        session.setCallbackUrl(request.getCallbackUrl() != null ? request.getCallbackUrl() : "");
        session.setStatus("active");
        session.setCreatedAt(now.format(FMT));
        session.setExpiresAt(expiresAt.format(FMT));
        session.setUpdatedAt(now.format(FMT));

        sessionRepository.save(session);
        log.info("创建预览会话: sessionId={}, file={}, type={}, mode={}", sessionId, fileName, viewerType, mode);

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("previewUrl", "/view/" + sessionId);
        result.put("fileName", fileName);
        result.put("fileType", ext);
        result.put("viewerType", viewerType.name());
        result.put("mode", mode);
        result.put("expiresAt", expiresAt.format(FMT));
        return result;
    }

    /**
     * 获取会话信息
     */
    public PreviewSession getSession(String sessionId) {
        PreviewSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));

        if (!"active".equals(session.getStatus())) {
            throw new RuntimeException("会话已过期或关闭");
        }

        return session;
    }

    /**
     * 保存编辑后的文件
     */
    public void saveFile(String sessionId, byte[] content) throws IOException {
        PreviewSession session = getSession(sessionId);

        if (!"edit".equals(session.getMode())) {
            throw new RuntimeException("当前会话不支持编辑");
        }

        Path filePath = Path.of(session.getFilePath());
        Files.write(filePath, content);

        log.info("文件已保存: sessionId={}, file={}, size={}", sessionId, session.getFileName(), content.length);

        // 如果配置了回调 URL，通知三方
        if (session.getCallbackUrl() != null && !session.getCallbackUrl().isBlank()) {
            notifyCallback(session);
        }
    }

    /**
     * 通知三方应用文件已更新
     */
    private void notifyCallback(PreviewSession session) {
        try {
            String body = String.format(
                    "{\"sessionId\":\"%s\",\"fileName\":\"%s\",\"status\":\"saved\",\"userId\":\"%s\"}",
                    session.getSessionId(), session.getFileName(), session.getUserId()
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(session.getCallbackUrl()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(resp -> log.info("回调通知完成: url={}, status={}", session.getCallbackUrl(), resp.statusCode()))
                    .exceptionally(e -> { log.error("回调通知失败: {}", e.getMessage()); return null; });
        } catch (Exception e) {
            log.error("回调通知异常: {}", e.getMessage());
        }
    }

    /**
     * 定时清理过期会话
     */
    @Scheduled(fixedDelay = 60000)
    public void cleanupExpiredSessions() {
        sessionRepository.expireOldSessions();
    }

    /**
     * 获取活跃会话列表
     */
    public List<PreviewSession> getActiveSessions() {
        return sessionRepository.findByStatus("active");
    }

    /**
     * 统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeSessions", sessionRepository.countActiveSessions());
        stats.put("totalSessions", sessionRepository.count());
        return stats;
    }
}
