package com.docsy.controller;

import com.docsy.config.DocsyProperties;
import com.docsy.config.ViewerRegistry;
import com.docsy.model.entity.PreviewSession;
import com.docsy.service.PreviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 预览页面渲染 — Thymeleaf 模板
 * 根据文件类型路由到对应渲染器模板
 */
@Controller
public class ViewerController {

    private final PreviewService previewService;
    private final ViewerRegistry viewerRegistry;
    private final DocsyProperties properties;

    public ViewerController(PreviewService previewService,
                            ViewerRegistry viewerRegistry,
                            DocsyProperties properties) {
        this.previewService = previewService;
        this.viewerRegistry = viewerRegistry;
        this.properties = properties;
    }

    /**
     * 预览页面入口 — 根据会话的文件类型路由到对应模板
     */
    @GetMapping("/view/{sessionId}")
    public String view(@PathVariable String sessionId, Model model) {
        try {
            PreviewSession session = previewService.getSession(sessionId);

            // 公共数据
            model.addAttribute("session", session);
            model.addAttribute("sessionId", sessionId);
            model.addAttribute("fileName", session.getFileName());
            model.addAttribute("fileType", session.getFileType());
            model.addAttribute("mode", session.getMode());
            model.addAttribute("editable", "edit".equals(session.getMode()));
            model.addAttribute("fileContentUrl", "/api/files/" + sessionId + "/content");
            model.addAttribute("onlyofficePath", properties.getOnlyofficePath());

            // 根据渲染器类型返回对应模板
            ViewerRegistry.ViewerType viewerType = viewerRegistry.resolve(session.getFileType());
            return viewerType.getTemplateName();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "viewer-error";
        }
    }
}
