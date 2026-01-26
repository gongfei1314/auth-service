package com.lerong.authservice.config;

import com.lerong.authservice.security.CustomUserDetailsService;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * 授权服务器配置类
 * 用于配置 OAuth2 授权服务器、客户端注册和 JWT 令牌签名
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 注册 OAuth2 客户端
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        // 注册统一门户客户端
        RegisteredClient portalClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("portal")
                .clientSecret("{noop}portal-secret") // {noop} 表示不加密，生产环境应使用 {bcrypt}
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // 授权码模式
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN) // 支持刷新令牌
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // 客户端凭证模式
                .redirectUri("http://localhost:8080/login/oauth2/code/portal") // 回调地址
                .redirectUri("http://localhost:8080/authorized") // 另一个回调地址示例
                .scope(OidcScopes.OPENID) // OpenID Connect
                .scope("user.read") // 读取用户信息权限
                .scope("user.write") // 写入用户信息权限
                .scope("all") // 全部权限
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true) // 需要用户授权确认
                        .requireProofKey(true) // 启用 PKCE (Proof Key for Code Exchange)
                        .build())
                .build();

        // 可以注册更多客户端，例如内部服务
        RegisteredClient internalService = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("internal-service")
                .clientSecret("{noop}internal-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope("all")
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(portalClient, internalService);
    }

    /**
     * 配置 JWT 令牌签名的 JWK 源
     * 用于生成 RSA 密钥对并签名 JWT 令牌
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .algorithm(JWSAlgorithm.RS256)
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * 生成 RSA 密钥对
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /**
     * 配置授权服务器的安全过滤器链
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults()); // 启用 OpenID Connect 1.0

        http.exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * 配置常规安全过滤器链
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/", "/index", "/public", "/health", "/test/**", "/h2-console/**",
                                "/login.html", "/login", "/css/**", "/js/**").permitAll() // 公开端点
                        .anyRequest().authenticated()
                )
                .userDetailsService(customUserDetailsService) // 使用自定义 UserDetailsService
                .csrf(csrf -> csrf.disable()) // 禁用 CSRF，方便测试
                .formLogin(form -> form
                        .loginPage("/login.html") // 自定义登录页面路径
                        .loginProcessingUrl("/perform_login") // 登录处理 URL，避免与默认的 /login 冲突
                        .defaultSuccessUrl("/user/info", true) // 登录成功后跳转
                        .failureUrl("/login.html?error=true") // 登录失败跳转
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * 配置授权服务器设置
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8081") // 授权服务器地址
                .build();
    }
}
