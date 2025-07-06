package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.dto.ReportDto;
import com.intimetec.newsaggreation.model.User;

import java.util.List;

public interface ArticleReportingService {
    void submitReport(User user, Long articleId, String reason);

    void markArticleAsHidden(Long articleId);

    void markArticleAsVisible(Long articleId);

    void hideAllByCategory(Integer categoryId);

    void unhideAllByCategory(Integer categoryId);

    List<ReportDto> getReportsForArticle(Long articleId);
}

