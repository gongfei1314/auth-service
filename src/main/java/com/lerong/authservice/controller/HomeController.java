package com.lerong.authservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 */
@Controller
public class HomeController {

    /**
     * 首页 - 重定向到登录页面
     */
    @GetMapping({"/", "/index"})
    public String index() {
        return "redirect:/login.html";
    }
}
