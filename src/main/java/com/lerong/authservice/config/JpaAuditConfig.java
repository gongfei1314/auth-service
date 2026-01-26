package com.lerong.authservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 审计配置类
 * 用于自动填充实体的创建时间和更新时间字段
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}
