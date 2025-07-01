package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.NotificationConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;


import java.security.Principal;

/**
 * Endpoints for a user to view and update his notification preferences
 * (category toggles + keyword list).
 * <p>
 * All routes require ROLE_USER.
 */
@RestController
@RequestMapping("/api/notify/prefs")
@RequiredArgsConstructor
public class NotificationConfigController {

    private final NotificationConfigService service;
    private final UserRepository userRepository;

    /**
     * GET  /api/notify/prefs  – return current user's config
     */
    @GetMapping
    public NotificationConfigDto getPrefs(Principal principal) {
        String email = principal.getName();  // this is the username used during login
        return service.getCurrentUserConfigByEmail(email);
    }


    /**
     * PUT  /api/notify/prefs  – overwrite config
     */
    @PutMapping
//    public NotificationConfigDto updatePrefs(@AuthenticationPrincipal User principal,
//                                             @RequestBody NotificationConfigDto dto)
    public NotificationConfigDto updatePrefs(@AuthenticationPrincipal UserDetails principal,
                                             @RequestBody NotificationConfigDto dto) {

        Long userId = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        service.updateConfig(userId, dto);               // persist
        return service.getCurrentUserConfig(userId);     // return updated state
    }




//    @PutMapping
//    public NotificationConfigDto updatePrefs(@AuthenticationPrincipal UserDetails principal,
//                                             @RequestBody NotificationConfigDto dto) {
//        Long userId = userRepo.findByEmail(principal.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"))
//                .getId();
//        service.updateConfig(userId, dto);
//        return service.getCurrentUserConfig(userId);
//    }


}
