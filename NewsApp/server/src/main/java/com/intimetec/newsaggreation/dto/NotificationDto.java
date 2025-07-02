// src/main/java/com/intimetec/newsaggreation/dto/NotificationDto.java
package com.intimetec.newsaggreation.dto;

public record NotificationDto(
        Long   id,
        String type,      // CATEGORY | KEYWORD
        String title,
        String url,
        String createdAt  // ISO‑8601 string
) {}
