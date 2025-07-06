package com.intimetec.newsaggreation.dto;

import lombok.Data;

@Data
public class ApiSourceDto {
    private Integer id;
    private String name;
    private String endpointUrl;
    private String apiKey;
    private Integer pollingFreq;
    private String status;
}
