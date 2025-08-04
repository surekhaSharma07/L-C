package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.model.Role;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.RoleRepository;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepository = userRepo;
        this.roleRepository = roleRepo;
    }

    @Override
    public User register(String email, String rawPassword) {
        log.info("Registering user with email: {}", email);
        String encoded = encoder.encode(rawPassword);

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> {
                    log.error("Default role USER not found");
                    return new IllegalStateException("Default role USER not found");
                });

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(encoded);
        user.setRole(userRole);
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });
    }
}





