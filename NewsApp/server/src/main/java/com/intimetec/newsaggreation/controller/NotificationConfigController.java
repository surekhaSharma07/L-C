package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.NotificationConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

@RestController
@RequestMapping("/api/notify/prefs")
@RequiredArgsConstructor
public class NotificationConfigController {

    private final NotificationConfigService service;
    private final UserRepository userRepository;

    @GetMapping
    public NotificationConfigDto getPrefs(Principal principal) {
        String email = principal.getName();
        return service.getCurrentUserConfigByEmail(email);
    }

    @PutMapping
    public NotificationConfigDto updatePrefs(@AuthenticationPrincipal UserDetails principal,
                                             @RequestBody NotificationConfigDto dto) {

        Long userId = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        service.updateConfig(userId, dto);
        return service.getCurrentUserConfig(userId);
    }
}
