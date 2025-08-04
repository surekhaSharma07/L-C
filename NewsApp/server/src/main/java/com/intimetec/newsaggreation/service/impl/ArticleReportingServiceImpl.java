package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.dto.ReportDto;
import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.BlockedKeyword;
import com.intimetec.newsaggreation.model.Report;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.ArticleRepository;
import com.intimetec.newsaggreation.repository.BlockedKeywordRepository;
import com.intimetec.newsaggreation.repository.ReportRepository;
import com.intimetec.newsaggreation.service.ArticleReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleReportingServiceImpl implements ArticleReportingService {

    private final ArticleRepository articleRepository;
    private final ReportRepository reportRepository;
    private final BlockedKeywordRepository keywordRepository;

    @Value("${report.autoHideThreshold:3}")
    private int autoHideThreshold;

    @Transactional
    @Override
    public void submitReport(User user, Long articleId, String reason) {
        log.info("Submitting report for user {} on article {}", user.getId(), articleId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    log.error("Article not found for report submission: articleId={}", articleId);
                    return new RuntimeException("Article not found");
                });

        if (reportRepository.existsByUserAndArticle(user, article)) {
            log.warn("User {} has already reported article {}", user.getId(), articleId);
            throw new RuntimeException("You have already reported this article.");
        }

        reportRepository.save(new Report(user, article, reason, LocalDateTime.now()));

        int count = reportRepository.countByArticle(article);
        if (count >= autoHideThreshold && !article.isHidden()) {
            log.info("Article {} is hidden due to report threshold.", articleId);
            article.setHidden(true);
        }
    }

    @Transactional
    @Override
    public void markArticleAsHidden(Long articleId) {
        log.info("Marking article {} as hidden.", articleId);
        articleRepository.findById(articleId)
                .ifPresent(article -> {
                    article.setHidden(true);
                    log.info("Article {} is now hidden.", articleId);
                });
    }

    @Transactional
    @Override
    public void markArticleAsVisible(Long articleId) {
        log.info("Marking article {} as visible.", articleId);
        articleRepository.findById(articleId)
                .ifPresent(article -> {
                    article.setHidden(false);
                    log.info("Article {} is now visible.", articleId);
                });
    }

    @Transactional
    public void addBlockedKeyword(String term) {
        log.info("Attempting to add blocked keyword: {}", term);
        String termLowerCase = term.toLowerCase();
        if (keywordRepository.existsByTermIgnoreCase(termLowerCase)) {
            log.warn("Blocked keyword {} already exists.", term);
            return;
        }

        keywordRepository.save(new BlockedKeyword(null, termLowerCase));
        log.info("Blocked keyword {} added.", term);

        articleRepository.findAll().stream()
                .filter(article -> !article.isHidden())
                .filter(article -> contains(article, termLowerCase))
                .forEach(article -> {
                    article.setHidden(true);
                    log.info("Article {} hidden due to new blocked keyword.", article.getId());
                });
    }

    @Transactional
    public void removeBlockedKeyword(String term) {
        log.info("Attempting to remove blocked keyword: {}", term);
        keywordRepository.findAll().stream()
                .filter(keyword -> keyword.getTerm().equalsIgnoreCase(term))
                .findFirst()
                .ifPresent(keywordRepository::delete);
        log.info("Blocked keyword {} removed.", term);
    }

    private boolean contains(Article article, String keyword) {
        String l = keyword.toLowerCase();
        return article.getTitle().toLowerCase().contains(l)
                || article.getDescription().toLowerCase().contains(l);
    }

    @Transactional
    public void hideAllByCategory(Integer categoryId) {
        log.info("Hiding all articles in category {}", categoryId);
        articleRepository.findByPrimaryCategory_Id(categoryId)
                .forEach(article -> {
                    article.setHidden(true);
                    log.info("Article {} hidden by category.", article.getId());
                });
    }

    @Transactional
    public void unhideAllByCategory(Integer categoryId) {
        log.info("Unhiding all articles in category {}", categoryId);
        articleRepository.findByPrimaryCategory_Id(categoryId)
                .forEach(article -> {
                    article.setHidden(false);
                    log.info("Article {} unhidden by category.", article.getId());
                });
    }

    @Override
    public List<ReportDto> getReportsForArticle(Long articleId) {
        log.info("Fetching reports for article {}", articleId);
        return reportRepository.findByArticle_Id(articleId)
                .stream()
                .map(ReportDto::from)
                .toList();
    }
}

