package com.lerong.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域配置
 * 允许前端应用（localhost:3000）访问后端 API
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有来源（开发环境）
        config.addAllowedOriginPattern("*");

        // 允许携带认证信息（cookies, authorization headers）
        config.setAllowCredentials(true);

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有HTTP方法（GET, POST, PUT, DELETE, etc.）
        config.addAllowedMethod("*");

        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
