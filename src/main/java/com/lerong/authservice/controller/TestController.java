package com.lerong.authservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证用户登录和授权功能
 */
@RestController
public class TestController {

    /**
     * 获取当前登录用户信息
     * 需要认证才能访问
     */
    @GetMapping("/user/info")
    public Map<String, Object> userInfo(Principal principal) {
        Map<String, Object> result = new HashMap<>();
        if (principal != null) {
            result.put("code", 200);
            result.put("message", "获取用户信息成功");
            result.put("data", Map.of(
                "username", principal.getName(),
                "authenticated", true,
                "authorities", principal.toString()
            ));
        } else {
            result.put("code", 401);
            result.put("message", "未登录");
            result.put("data", null);
        }
        return result;
    }

    /**
     * 获取当前认证对象的详细信息
     */
    @GetMapping("/user/details")
    public Map<String, Object> userDetails(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            result.put("code", 200);
            result.put("message", "获取认证详情成功");

            Map<String, Object> data = new HashMap<>();
            data.put("username", authentication.getName());
            data.put("authorities", authentication.getAuthorities());
            data.put("isAuthenticated", authentication.isAuthenticated());
            data.put("details", authentication.getDetails());
            data.put("principal", authentication.getPrincipal());

            result.put("data", data);
        } else {
            result.put("code", 401);
            result.put("message", "未认证");
            result.put("data", null);
        }

        return result;
    }

    /**
     * 公开端点，不需要认证
     */
    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "这是公开端点，无需认证");
        result.put("data", Map.of("service", "auth-service", "status", "running"));
        return result;
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "auth-service");
        return result;
    }
}
