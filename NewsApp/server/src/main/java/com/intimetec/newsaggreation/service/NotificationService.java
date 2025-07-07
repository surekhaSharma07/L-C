package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    public List<NotificationDto> findByUserEmail(String email);
}
