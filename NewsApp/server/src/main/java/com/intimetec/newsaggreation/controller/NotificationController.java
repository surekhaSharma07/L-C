package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.NotificationDto;
import com.intimetec.newsaggreation.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public List<NotificationDto> getNotificationsForCurrentUser(Principal principal) {
        return service.findByUserEmail(principal.getName());
    }
}


