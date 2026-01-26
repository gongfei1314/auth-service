package com.lerong.authservice.controller;

import com.lerong.authservice.entity.User;
import com.lerong.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证测试控制器
 * 用于测试认证功能
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class AuthTestController {

    private final UserService userService;

    /**
     * 测试用户加载
     * GET /test/load-user/{username}
     */
    @GetMapping("/load-user/{username}")
    public ResponseEntity<Map<String, Object>> loadUser(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);

        Map<String, Object> result = new HashMap<>();
        if (user.isPresent()) {
            result.put("code", 200);
            result.put("message", "用户加载成功");
            result.put("data", Map.of(
                "username", user.get().getUsername(),
                "realName", user.get().getRealName(),
                "email", user.get().getEmail(),
                "enabled", user.get().getEnabled() == 1,
                "locked", user.get().getLocked() == 1
            ));
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 测试数据库连接
     * GET /test/db-status
     */
    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> checkDatabase() {
        Map<String, Object> result = new HashMap<>();
        try {
            long userCount = userService.findAllCount();
            result.put("code", 200);
            result.put("message", "数据库连接正常");
            result.put("data", Map.of(
                "database", "MySQL",
                "userCount", userCount,
                "status", "connected"
            ));
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "数据库连接失败");
            result.put("error", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 测试认证状态（不需要登录）
     * GET /test/auth-status
     */
    @GetMapping("/auth-status")
    public ResponseEntity<Map<String, Object>> checkAuth(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            result.put("code", 200);
            result.put("message", "已认证");
            result.put("data", Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities(),
                "isAuthenticated", true
            ));
        } else {
            result.put("code", 401);
            result.put("message", "未认证");
            result.put("data", Map.of(
                "isAuthenticated", false
            ));
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 测试需要认证的端点
     * GET /test/protected
     */
    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedEndpoint(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "访问受保护资源成功");
        result.put("data", Map.of(
            "username", authentication.getName(),
            "message", "您已通过认证"
        ));
        return ResponseEntity.ok(result);
    }
}
