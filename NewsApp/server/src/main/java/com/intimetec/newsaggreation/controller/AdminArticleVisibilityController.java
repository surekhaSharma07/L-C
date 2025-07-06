// src/main/java/com/intimetec/newsaggreation/controller/AdminArticleModerationController.java
package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.ReportDto;
import com.intimetec.newsaggreation.service.ArticleReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/articles/visibility")
@RequiredArgsConstructor
public class AdminArticleVisibilityController {

    private final ArticleReportingService articleReportingService;

    @PostMapping("/hide/{articleId}")
    public void hideArticle(@PathVariable("articleId") Long articleId) {
        articleReportingService.markArticleAsHidden(articleId);
    }

    @PostMapping("/unhide/{articleId}")
    public void unhideArticle(@PathVariable("articleId") Long articleId) {
        articleReportingService.markArticleAsVisible(articleId);
    }

    @PostMapping("/hide/category/{categoryId}")
    public void hideAllInCategory(@PathVariable("categoryId") Integer categoryId) {
        articleReportingService.hideAllByCategory(categoryId);
    }

    @PostMapping("/unhide/category/{categoryId}")
    public void unhideAllInCategory(@PathVariable("categoryId") Integer categoryId) {
        articleReportingService.unhideAllByCategory(categoryId);
    }

    @GetMapping("/reports/{articleId}")
    public List<ReportDto> reports(@PathVariable("articleId") Long articleId) {
        return articleReportingService.getReportsForArticle(articleId);
    }
}
