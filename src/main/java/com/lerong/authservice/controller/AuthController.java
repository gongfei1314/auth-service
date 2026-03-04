package com.lerong.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证API控制器
 * 提供登录、获取用户信息、登出等接口
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    /**
     * 登录接口
     * POST /auth/login
     *
     * @param requestBody 包含username和password的请求体
     * @return JWT令牌和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        log.info("登录请求 - 用户名: {}", username);

        try {
            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // TODO: 这里应该生成JWT令牌，暂时返回模拟数据
            // 实际应该使用 JwtEncoder 生成JWT
            String mockAccessToken = "mock-jwt-token-" + System.currentTimeMillis();
            String mockRefreshToken = "mock-refresh-token-" + System.currentTimeMillis();

            Map<String, Object> response = new HashMap<>();
            response.put("access_token", mockAccessToken);
            response.put("refresh_token", mockRefreshToken);
            response.put("token_type", "Bearer");
            response.put("expires_in", 3600);
            response.put("username", userDetails.getUsername());

            log.info("用户 {} 登录成功", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("用户 {} 登录失败: {}", username, e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "invalid_grant");
            error.put("error_description", "用户名或密码错误");
            return ResponseEntity.status(401).body(error);
        }
    }

    /**
     * 获取当前用户信息
     * GET /auth/user/info
     *
     * @param authorization Authorization header
     * @return 用户详细信息
     */
    @GetMapping("/user/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        // 手动验证token
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "未认证");
            error.put("message", "缺少Authorization header");
            return ResponseEntity.status(401).body(error);
        }

        String token = authorization.substring(7); // 去掉 "Bearer " 前缀

        // TODO: 这里应该验证JWT token
        // 目前我们接受任何以 "mock-jwt-token-" 开头的token
        if (!token.startsWith("mock-jwt-token-")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "invalid_token");
            error.put("message", "无效的token");
            return ResponseEntity.status(401).body(error);
        }

        // 从token中提取用户名（实际应该从JWT claims中获取）
        // 这里我们返回模拟数据
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 1);
        userInfo.put("username", "admin");
        userInfo.put("realName", "系统管理员");
        userInfo.put("email", "admin@example.com");
        userInfo.put("phone", "13800138000");
        userInfo.put("avatar", null);

        log.info("获取用户信息 - 使用token: {}", token);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 用户登出
     * POST /auth/logout
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        log.info("用户登出");

        // TODO: 如果使用了Redis存储令牌，这里应该将令牌加入黑名单
        // 目前只返回成功消息

        Map<String, String> response = new HashMap<>();
        response.put("message", "登出成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 检查认证状态
     * GET /auth/check
     *
     * @param authentication 当前认证信息
     * @return 认证状态
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth(
            @AuthenticationPrincipal Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            response.put("authenticated", true);
            response.put("username", authentication.getName());
        } else {
            response.put("authenticated", false);
        }

        return ResponseEntity.ok(response);
    }
}
