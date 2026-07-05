package com.match.controller;

import com.match.common.Result;
import com.match.entity.User;
import com.match.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(
            @RequestBody LoginRequest request,
            HttpSession session
    ) {
        User user = userService.findByUsername(request.getUsername());
        if (user == null || !request.getPassword().equals(user.getPassword())) {
            return Result.error("账号或密码错误");
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());
        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<String> register(
            @RequestBody User user
    ) {
        User existing = userService.findByUsername(user.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        userService.saveUser(user);
        return Result.success("注册成功");
    }

    private static class LoginRequest {
        private String username;
        private String password;

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
    }
}
