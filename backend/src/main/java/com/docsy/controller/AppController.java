package com.docsy.controller;

import com.docsy.model.dto.R;
import com.docsy.model.entity.App;
import com.docsy.service.AppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 三方应用管理接口（需 JWT 认证）
 */
@RestController
@RequestMapping("/api/apps")
public class AppController {

    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    /** 获取所有应用 */
    @GetMapping
    public R<List<App>> list() {
        return R.ok(appService.list());
    }

    /** 创建应用 */
    @PostMapping
    public R<App> create(@RequestBody Map<String, String> body) {
        App app = appService.create(body.get("appName"), body.get("description"));
        return R.ok(app);
    }

    /** 切换启用/禁用 */
    @PutMapping("/{id}/toggle")
    public R<App> toggleActive(@PathVariable Long id) {
        return R.ok(appService.toggleActive(id));
    }

    /** 重置密钥 */
    @PutMapping("/{id}/reset-secret")
    public R<App> resetSecret(@PathVariable Long id) {
        return R.ok(appService.resetSecret(id));
    }

    /** 删除应用 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        appService.delete(id);
        return R.ok();
    }
}
