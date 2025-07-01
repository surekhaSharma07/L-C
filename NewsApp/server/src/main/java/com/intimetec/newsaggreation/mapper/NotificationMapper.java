package com.intimetec.newsaggreation.mapper;

import com.intimetec.newsaggreation.dto.KeywordDto;
import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.model.NotificationConfig;

import java.util.stream.Collectors;

/**
 * Converts {@link NotificationConfig} JPA entities into API‑friendly DTOs.
 * Keeps controllers clean and avoids leaking internal models.
 */
public final class NotificationMapper {

    private NotificationMapper() { /* utility class */ }

    public static NotificationConfigDto toDto(NotificationConfig config) {
        return new NotificationConfigDto(
                config.isBusiness(),
                config.isEntertainment(),
                config.isSports(),
                config.isTechnology(),
                config.getKeywords().stream()
                        .map(k -> new KeywordDto(k.getId(), k.getTerm()))
                        .collect(Collectors.toList())
        );
    }
}
