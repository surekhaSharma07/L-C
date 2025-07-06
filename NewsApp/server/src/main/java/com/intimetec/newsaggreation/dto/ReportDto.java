package com.intimetec.newsaggreation.dto;

import com.intimetec.newsaggreation.model.Report;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportDto {

    private Long id;
    private Long userId;
    private String userEmail;
    private Long articleId;
    private String articleTitle;
    private String reason;
    private LocalDateTime reportedAt;

    public static ReportDto from(Report report) {
        return ReportDto.builder()
                .id(report.getId())
                .userId(report.getUser().getId())
                .userEmail(report.getUser().getEmail())
                .articleId(report.getArticle().getId())
                .articleTitle(report.getArticle().getTitle())
                .reason(report.getReason())
                .reportedAt(report.getReportedAt())
                .build();
    }
}
