package com.intimetec.newsaggreation.dto;

import lombok.Data;

/**
 * Lightweight view of ApiSource for admin endpoints.
 */
@Data
public class ApiSourceDto {
    private Integer id;
    private String name;
    private String endpointUrl;
    private String apiKey;
    private Integer pollingFreq;        // seconds / minutes – whatever you store
    private String status;              // “UP” | “DOWN” (added at runtime)
}
