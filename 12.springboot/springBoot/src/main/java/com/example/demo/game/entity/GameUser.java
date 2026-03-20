package com.example.demo.game.entity;

import java.util.Date;

/**
 * 游戏用户实体类
 * 对应数据库表 game_users
 */
public class GameUser {
    /**
     * 用户ID，主键，自增
     */
    private Integer id;

    /**
     * 用户名，唯一
     */
    private String username;

    /**
     * 密码（建议存储哈希值）
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 最高分
     */
    private Integer highScore;

    /**
     * 创建时间
     */
    private Date createTime;

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getHighScore() {
        return highScore;
    }

    public void setHighScore(Integer highScore) {
        this.highScore = highScore;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "GameUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", highScore=" + highScore +
                ", createTime=" + createTime +
                '}';
    }
}