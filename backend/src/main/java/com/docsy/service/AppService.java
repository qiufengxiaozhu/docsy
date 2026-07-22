package com.docsy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.docsy.mapper.AppMapper;
import com.docsy.model.entity.App;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 三方应用管理服务
 */
@Service
public class AppService {

    private final AppMapper appMapper;

    public AppService(AppMapper appMapper) {
        this.appMapper = appMapper;
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
        appMapper.insert(app);
        return app;
    }

    /**
     * 获取所有应用
     */
    public List<App> list() {
        return appMapper.selectList(null);
    }

    /**
     * 根据 appId 获取应用
     */
    public App getByAppId(String appId) {
        App app = appMapper.selectOne(new LambdaQueryWrapper<App>().eq(App::getAppId, appId));
        if (app == null) {
            throw new RuntimeException("应用不存在: " + appId);
        }
        return app;
    }

    /**
     * 切换应用状态
     */
    public App toggleActive(Long id) {
        App app = appMapper.selectById(id);
        if (app == null) {
            throw new RuntimeException("应用不存在");
        }
        app.setIsActive(app.getIsActive() == 1 ? 0 : 1);
        appMapper.updateById(app);
        return app;
    }

    /**
     * 重置密钥
     */
    public App resetSecret(Long id) {
        App app = appMapper.selectById(id);
        if (app == null) {
            throw new RuntimeException("应用不存在");
        }
        app.setAppSecret(UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""));
        appMapper.updateById(app);
        return app;
    }

    /**
     * 删除应用
     */
    public void delete(Long id) {
        appMapper.deleteById(id);
    }
}
