package com.intimetec.newsaggregation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private String userEmail;
    private String reportedAt;  // ISO‑8601 string from the server
    private String reason;
}
