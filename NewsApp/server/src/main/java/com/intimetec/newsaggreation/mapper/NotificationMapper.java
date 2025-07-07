package com.intimetec.newsaggreation.mapper;

import com.intimetec.newsaggreation.dto.KeywordDto;
import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.model.NotificationConfig;

import java.util.stream.Collectors;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationConfigDto toDto(NotificationConfig notificationConfig) {
        return new NotificationConfigDto(
                notificationConfig.isBusiness(),
                notificationConfig.isEntertainment(),
                notificationConfig.isSports(),
                notificationConfig.isTechnology(),
                notificationConfig.getKeywords().stream()
                        .map(k -> new KeywordDto(k.getId(), k.getTerm()))
                        .collect(Collectors.toList())
        );
    }
}
