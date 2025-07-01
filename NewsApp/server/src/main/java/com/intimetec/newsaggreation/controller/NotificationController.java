package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.service.impl.NotificationServiceImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class NotificationController {
    NotificationServiceImpl notificationService;

    @PutMapping("/prefs")
    public NotificationConfigDto updatePrefs(@RequestBody NotificationConfigDto dto,
                                             @AuthenticationPrincipal(expression = "username") String email) {
        return notificationService.updatePreferences(email, dto);
    }

}
