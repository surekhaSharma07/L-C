package com.intimetec.newsaggreation.dto;

/**
 * A minimal DTO representing a single keyword.
 * Uses Java 16 "record" for brevity and immutability.
 */
public record KeywordDto(Long id, String term) { }