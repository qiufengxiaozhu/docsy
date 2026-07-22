package com.docsy.service;

import com.docsy.model.entity.App;
import com.docsy.repository.AppRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 三方应用管理服务
 */
@Service
public class AppService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AppRepository appRepository;

    public AppService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    /**
     * 创建应用
     */
    public App create(String appName, String description) {
        App app = new App();
        app.setAppId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        app.setAppName(appName);
        app.setAppSecret(UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""));
        app.setDescription(description != null ? description : "");
        app.setIsActive(1);
        app.setRateLimit(100);
        app.setCreatedAt(LocalDateTime.now().format(FMT));
        app.setUpdatedAt(LocalDateTime.now().format(FMT));
        return appRepository.save(app);
    }

    /**
     * 获取所有应用
     */
    public List<App> list() {
        return (List<App>) appRepository.findAll();
    }

    /**
     * 根据 appId 获取应用
     */
    public App getByAppId(String appId) {
        return appRepository.findByAppId(appId)
                .orElseThrow(() -> new RuntimeException("应用不存在: " + appId));
    }

    /**
     * 切换应用状态
     */
    public App toggleActive(Long id) {
        App app = appRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("应用不存在"));
        app.setIsActive(app.getIsActive() == 1 ? 0 : 1);
        app.setUpdatedAt(LocalDateTime.now().format(FMT));
        return appRepository.save(app);
    }

    /**
     * 重置密钥
     */
    public App resetSecret(Long id) {
        App app = appRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("应用不存在"));
        app.setAppSecret(UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""));
        app.setUpdatedAt(LocalDateTime.now().format(FMT));
        return appRepository.save(app);
    }

    /**
     * 删除应用
     */
    public void delete(Long id) {
        appRepository.deleteById(id);
    }
}
