package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.repository.ArticleRepository;
import com.intimetec.newsaggreation.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository repo;

    @Override
    public boolean existsByUrl(String url) {
        return repo.existsByUrl(url);
    }

    @Override
    public Article save(Article news) {
        return repo.save(news);
    }

    @Override
    public List<Article> findAll() {
        return repo.findAll();
    }

    @Override
    public List<Article> findToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end   = start.plusDays(1);
        var allByPublishedAtBetween = repo.findAllByPublishedAtBetween(start, end);
        return allByPublishedAtBetween;
    }

    @Override
    public List<Article> findByDateRange(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.plusDays(1).atStartOfDay();
        return repo.findAllByPublishedAtBetween(start, end);
    }

    @Override
    public List<Article> findByDateAndCategory(LocalDate date, String category) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = start.plusDays(1);
        return repo.findAllByPublishedAtBetweenAndPrimaryCategory_Name(start, end, category);
    }

    @Override
    public List<Article> findByDateRangeAndCategory(LocalDate from, LocalDate to, String category) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.plusDays(1).atStartOfDay();
        return repo.findAllByPublishedAtBetweenAndCategories_Name(start, end, category);
    }

    @Override
    public List<Article> findByPrimaryCategory(String categoryName) {
        return repo.findByPrimaryCategory_Name(categoryName);
    }

    @Override
    public List<Article> findByCategory(String categoryName) {
        return repo.findByCategories_Name(categoryName);
    }

    @Override
    public Article findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found: " + id));
    }
}
