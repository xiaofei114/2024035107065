package com.example.demo.game.service;

import com.example.demo.game.entity.GameUser;
import com.example.demo.game.mapper.GameUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏登录服务类
 * 处理用户登录逻辑
 */
@Service
public class GameLoginService {

    @Autowired
    private GameUserMapper gameUserMapper;

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，包含状态码、消息和数据
     */
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();

        // 参数校验
        if (username == null || username.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "用户名不能为空");
            return result;
        }
        if (password == null || password.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "密码不能为空");
            return result;
        }

        // 查询用户
        GameUser user = gameUserMapper.selectByUsernameAndPassword(username, password);
        if (user == null) {
            // 再次检查用户名是否存在（可选）
            GameUser userByUsername = gameUserMapper.selectByUsername(username);
            if (userByUsername == null) {
                result.put("code", 401);
                result.put("msg", "用户名不存在");
            } else {
                result.put("code", 401);
                result.put("msg", "密码错误");
            }
            return result;
        }

        // 登录成功，构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("nickname", user.getNickname());
        data.put("highScore", user.getHighScore());

        result.put("code", 200);
        result.put("msg", "登录成功");
        result.put("data", data);
        return result;
    }
}