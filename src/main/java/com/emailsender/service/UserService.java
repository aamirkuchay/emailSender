package com.emailsender.service;

import com.emailsender.entity.User;

public interface UserService {

    User saveUser(User user);
    Boolean verifyToken(String token);
}
