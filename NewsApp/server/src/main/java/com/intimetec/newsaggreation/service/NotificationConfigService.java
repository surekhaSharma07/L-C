package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.dto.NotificationConfigDto;

public interface NotificationConfigService {
    NotificationConfigDto getCurrentUserConfig(Long userId);
    void updateConfig(Long userId, NotificationConfigDto dto);
    NotificationConfigDto getCurrentUserConfigByEmail(String email);
}
