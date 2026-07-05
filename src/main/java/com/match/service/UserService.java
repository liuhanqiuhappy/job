package com.match.service;

import com.match.entity.User;

public interface UserService {

    User findByUsername(String username);

    boolean saveUser(User user);
}
