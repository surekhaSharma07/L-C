package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.model.Role;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.RoleRepository;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    public User register(String email, String rawPassword) {
        String hash = encoder.encode(rawPassword);

        Role userRole = roleRepo.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hash);
        user.setRole(userRole);
        return userRepo.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}





