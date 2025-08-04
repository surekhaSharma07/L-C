package com.intimetec.newsaggreation.dto;

import java.util.List;

public record NotificationConfigDto(
        boolean business,
        boolean entertainment,
        boolean sports,
        boolean technology,
        List<KeywordDto> keywords
) {
}
