# CLAUDE.md

此文件为 Claude Code (claude.ai/code) 提供在此代码仓库中工作的指导。

## 项目概述

这是一个基于 Spring Boot 3.5.9 和 Spring Authorization Server 1.5.0 构建的 OAuth2 授权服务器。它使用 OAuth2 和 OpenID Connect 协议提供集中的身份认证和授权服务。

## 技术栈

- Java 17
- Spring Boot 3.5.9
- Spring Security OAuth2 Authorization Server 1.5.0
- Spring Data JPA
- MySQL 数据库
- Lombok

## 常用命令

### 构建和运行
```bash
# 构建项目
mvn clean package

# 运行应用
mvn spring-boot:run

# 运行测试
mvn test

# 运行特定测试
mvn test -Dtest=AuthServiceApplicationTests
```

### 服务器配置
应用默认运行在 8081 端口（在 application.yml 中配置）。

## 架构设计

### 安全配置

应用在 `AuthorizationServerConfig` 中配置了两条安全过滤器链：

1. **授权服务器过滤器链（Order 1）**：处理 OAuth2/OIDC 端点（如 `/oauth2/authorize`、`/oauth2/token`）
2. **默认安全过滤器链（Order 2）**：处理常规的应用安全

### OAuth2 客户端

在 `InMemoryRegisteredClientRepository` 中注册了两个客户端：

1. **portal** - 门户应用客户端
   - Client ID: `portal`
   - Client Secret: `portal-secret`
   - 授权类型：授权码模式、刷新令牌、客户端凭证模式
   - 需要用户授权确认和 PKCE

2. **internal-service** - 内部服务客户端
   - Client ID: `internal-service`
   - Client Secret: `internal-secret`
   - 授权类型：客户端凭证模式、刷新令牌

### 认证流程

用户通过 `CustomUserDetailsService` 从数据库加载：
- 通过 `UserRepository` 查询 `sys_user` 表
- 将数据库用户字段映射到 Spring Security 的 `UserDetails`
- 处理账户状态（enabled、locked 标志）

### 数据库结构

主要实体是 `sys_user` 表中的 `User`：
- `username`（唯一索引）- 登录用户名
- `password` - 加密后的密码
- `enabled` - 账户状态（0=禁用，1=启用）
- `locked` - 锁定状态（0=未锁定，1=已锁定）
- `realName`、`email`、`phone` - 用户个人信息
- `createTime`、`updateTime` - 审计字段（通过 JPA 审计自动填充）

### 公开端点

以下端点公开访问（无需认证）：
- `/`、`/index`、`/public`、`/health`
- `/test/**`
- `/login.html`、`/login`、`/perform_login`
- `/css/**`、`/js/**`

### JWT 令牌配置

- 启动时自动生成 RSA 2048 位密钥
- 令牌使用 RS256 算法签名
- 签发者：`http://localhost:8081`

## 配置文件

- `application.yml` - 包含数据库连接（本地 MySQL localhost:3306/auth_db）、JPA 设置、服务器端口和日志配置
- JPA 配置了 `ddl-auto: update` 以自动更新数据库表结构

## 核心组件

- `AuthorizationServerConfig` - 主要的安全和 OAuth2 配置类
- `CustomUserDetailsService` - 从数据库加载用户认证信息
- `UserService` / `UserRepository` - 用户数据访问层
- `JpaAuditConfig` - JPA 审计配置，用于自动填充时间戳
