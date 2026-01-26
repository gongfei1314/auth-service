package com.lerong.authservice.security;

import com.lerong.authservice.entity.User;
import com.lerong.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义 UserDetailsService 实现
 * 从数据库加载用户信息
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查找用户
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 构建 UserDetails 对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(user.getEnabled() == 0) // 0-禁用，1-启用
                .accountLocked(user.getLocked() == 1) // 0-未锁定，1-已锁定
                .accountExpired(false) // 账号未过期
                .credentialsExpired(false) // 凭证未过期
                .authorities(Collections.emptyList()) // 权限列表，可后续关联角色表
                .build();
    }
}
