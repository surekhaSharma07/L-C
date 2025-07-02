package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.dto.NotificationDto;
import com.intimetec.newsaggreation.service.NotificationService;
import com.intimetec.newsaggreation.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {
    NotificationServiceImpl notificationService;

//    @PutMapping("/prefs")
//    public NotificationConfigDto updatePrefs(@RequestBody NotificationConfigDto dto,
//                                             @AuthenticationPrincipal(expression = "username") String email) {
//        return notificationService.updatePreferences(email, dto);
//    }

        private final NotificationService service;

        /** GET /api/notify  → all notifications for the logged‑in user (newest first). */
        @GetMapping
        public List<NotificationDto> listMine(Principal principal) {
            return service.findByUserEmail(principal.getName());   // email == username
        }
    }


