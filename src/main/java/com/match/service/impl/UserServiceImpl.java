package com.match.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.match.entity.User;
import com.match.mapper.UserMapper;
import com.match.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        logger.info("查询用户名: {}", username);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        logger.info("查询结果: {}", user != null ? "找到用户" : "未找到用户");
        return user;
    }

    @Override
    public boolean saveUser(User user) {
        logger.info("保存用户: {}", user.getUsername());
        logger.info("用户角色: {}, 手机号: {}, 城市: {}", user.getRole(), user.getPhone(), user.getCity());
        try {
            int result = userMapper.insert(user);
            logger.info("插入结果: {}", result);
            return result > 0;
        } catch (Exception e) {
            logger.error("保存用户失败", e);
            throw e;
        }
    }
}
