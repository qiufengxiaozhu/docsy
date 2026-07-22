package com.docsy.controller;

import com.docsy.model.dto.R;
import com.docsy.model.entity.PreviewSession;
import com.docsy.service.AppService;
import com.docsy.service.PreviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 管理后台仪表盘 + 会话监控
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PreviewService previewService;
    private final AppService appService;

    public DashboardController(PreviewService previewService, AppService appService) {
        this.previewService = previewService;
        this.appService = appService;
    }

    /** 统计概览 */
    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        Map<String, Object> stats = previewService.getStats();
        stats.put("totalApps", appService.list().size());
        return R.ok(stats);
    }

    /** 活跃会话列表 */
    @GetMapping("/sessions")
    public R<List<PreviewSession>> activeSessions() {
        return R.ok(previewService.getActiveSessions());
    }
}
