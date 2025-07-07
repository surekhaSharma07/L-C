package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service("userDetailsService")
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user details for email: {}", email);
        User user = repo.findByEmail(email).orElseThrow(() -> {
            log.error("User not found for email: {}", email);
            return new UsernameNotFoundException("User not found");
        });
        log.info("User details loaded successfully for email: {}", email);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
        );

    }
}
