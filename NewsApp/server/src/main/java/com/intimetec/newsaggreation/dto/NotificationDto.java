package com.intimetec.newsaggreation.dto;

public record NotificationDto(
        Long id,
        String type,
        String title,
        String url,
        String createdAt
) {
}
