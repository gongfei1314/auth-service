package com.lerong.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // 启用服务发现
public class AuthServiceApplication {

    // 启动入口
    public static void main(String[] args) {

        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
