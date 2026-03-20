package com.example.demo.game.mapper;

import com.example.demo.game.entity.GameUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 游戏用户数据访问接口
 * 使用MyBatis注解方式操作数据库
 */
@Mapper
public interface GameUserMapper {

    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，如果找不到返回null
     */
    @Select("SELECT id, username, password, nickname, high_score as highScore, create_time as createTime " +
            "FROM game_users WHERE username = #{username} AND password = #{password}")
    GameUser selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 根据用户名查询用户（备用）
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT id, username, password, nickname, high_score as highScore, create_time as createTime " +
            "FROM game_users WHERE username = #{username}")
    GameUser selectByUsername(@Param("username") String username);
}