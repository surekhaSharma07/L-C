package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.User;

public interface UserService {
    User register(String email, String rawPassword);

    User findByEmail(String email);
}
