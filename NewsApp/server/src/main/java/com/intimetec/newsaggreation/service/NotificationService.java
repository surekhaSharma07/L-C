package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    //    NotificationConfigDto getCurrentUserConfigByEmail(String email);
//
//    NotificationConfigDto updatePreferences(String email, NotificationConfigDto dto);
    public List<NotificationDto> findByUserEmail(String email);
}
