package com.example.demo.game.controller;

import com.example.demo.game.service.GameLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 游戏登录控制器
 * 提供用户登录接口
 */
@RestController
@RequestMapping("/api/game")
public class GameLoginController {

    @Autowired
    private GameLoginService gameLoginService;

    /**
     * 用户登录接口
     * POST /api/game/login
     * @param username 用户名
     * @param password 密码
     * @return 登录结果JSON
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password) {
        // 调用服务层进行登录验证
        return gameLoginService.login(username, password);
    }

    /**
     * 健康检查接口（可选）
     * GET /api/game/health
     * @return 服务状态
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("code", 200, "msg", "游戏登录服务运行正常");
    }
}