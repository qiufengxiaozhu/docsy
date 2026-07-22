package com.docsy.controller;

import com.docsy.model.dto.LoginRequest;
import com.docsy.model.dto.R;
import com.docsy.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 登录 */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = authService.login(request);
            return R.ok(result);
        } catch (Exception e) {
            return R.fail(401, e.getMessage());
        }
    }

    /** 获取当前用户信息 */
    @GetMapping("/info")
    public R<Map<String, Object>> info() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return R.ok(Map.of("username", username));
    }

    /** 修改密码 */
    @PutMapping("/password")
    public R<Void> changePassword(@RequestBody Map<String, String> body) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.changePassword(username, body.get("oldPassword"), body.get("newPassword"));
        return R.ok();
    }
}
