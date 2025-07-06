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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleReportingServiceImpl implements ArticleReportingService {

    private final ArticleRepository articleRepository;
    private final ReportRepository reportRepository;
    private final BlockedKeywordRepository keywordRepo;

    @Value("${report.autoHideThreshold:3}")
    private int autoHideThreshold;

    @Transactional
    @Override
    public void submitReport(User user, Long articleId, String reason) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        if (reportRepository.existsByUserAndArticle(user, article)) {
            throw new RuntimeException("You have already reported this article.");
        }

        reportRepository.save(new Report(user, article, reason, LocalDateTime.now()));

        int count = reportRepository.countByArticle(article);
        if (count >= autoHideThreshold && !article.isHidden()) {
            article.setHidden(true);
        }
    }

    @Transactional
    @Override
    public void markArticleAsHidden(Long articleId) {
        articleRepository.findById(articleId)
                .ifPresent(article -> article.setHidden(true));
    }

    @Transactional
    @Override
    public void markArticleAsVisible(Long articleId) {
        articleRepository.findById(articleId)
                .ifPresent(article -> article.setHidden(false));
    }

    @Transactional
    public void addBlockedKeyword(String term) {
        String lc = term.toLowerCase();
        if (keywordRepo.existsByTermIgnoreCase(lc)) return;

        keywordRepo.save(new BlockedKeyword(null, lc));

        // retro‑actively hide
        articleRepository.findAll().stream()
                .filter(a -> !a.isHidden())
                .filter(a -> contains(a, lc))
                .forEach(a -> a.setHidden(true));
    }

    @Transactional
    public void removeBlockedKeyword(String term) {
        keywordRepo.findAll().stream()
                .filter(k -> k.getTerm().equalsIgnoreCase(term))
                .findFirst()
                .ifPresent(keywordRepo::delete);
    }

    private boolean contains(Article a, String kw) {
        String l = kw.toLowerCase();
        return a.getTitle().toLowerCase().contains(l)
                || a.getDescription().toLowerCase().contains(l);
    }

    @Transactional
    public void hideAllByCategory(Integer categoryId) {
        articleRepository.findByPrimaryCategory_Id(categoryId)
                .forEach(article -> article.setHidden(true));
    }

    @Transactional
    public void unhideAllByCategory(Integer categoryId) {
        articleRepository.findByPrimaryCategory_Id(categoryId)
                .forEach(article -> article.setHidden(false));
    }

@Override
    public List<ReportDto> getReportsForArticle(Long articleId) {
        return reportRepository.findByArticle_Id(articleId)
                .stream()
                .map(ReportDto::from)
                .toList();
    }


}

