package com.lerong.authservice.service;

import com.lerong.authservice.entity.User;
import com.lerong.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户服务类
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据用户ID查找用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 创建新用户
     *
     * @param user 用户信息
     * @return 保存后的用户信息
     */
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 获取用户总数
     *
     * @return 用户总数
     */
    public long findAllCount() {
        return userRepository.count();
    }
}
