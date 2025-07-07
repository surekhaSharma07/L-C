package com.intimetec.newsaggregation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiSourceDto {
    private Integer id;
    private String name;
    private String endpointUrl;
    private String apiKey;
    private Integer pollingFreq;
    private String status;
}
