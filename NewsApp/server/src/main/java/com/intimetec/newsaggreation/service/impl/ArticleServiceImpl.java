package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.repository.ArticleRepository;
import com.intimetec.newsaggreation.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Override
    public boolean existsByUrl(String url) {
        log.info("Checking existence of article by URL");
        return articleRepository.existsByUrl(url);
    }

    @Override
    public Article save(Article news) {
        log.info("Saving article: {}", news.getTitle());
        return articleRepository.save(news);
    }

    @Override
    public List<Article> findAll() {
        log.info("Fetching all articles");
        return articleRepository.findAll();
    }

    @Override
    public List<Article> findToday() {
        log.info("Fetching today's articles");
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        var allByPublishedAtBetween = articleRepository.findAllByPublishedAtBetween(start, end);
        return allByPublishedAtBetween;
    }

    @Override
    public List<Article> findByDateRange(LocalDate from, LocalDate to) {
        log.info("Fetching articles from {} to {}", from, to);
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        return articleRepository.findAllByPublishedAtBetween(start, end);
    }

    @Override
    public List<Article> findByDateAndCategory(LocalDate date, String category) {
        log.info("Fetching articles for date {} and category {}", date, category);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return articleRepository.findAllByPublishedAtBetweenAndPrimaryCategory_Name(start, end, category);
    }

    @Override
    public List<Article> findByDateRangeAndCategory(LocalDate from, LocalDate to, String category) {
        log.info("Fetching articles from {} to {} and category {}", from, to, category);
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        return articleRepository.findAllByPublishedAtBetweenAndCategories_Name(start, end, category);
    }

    @Override
    public List<Article> findByPrimaryCategory(String categoryName) {
        log.info("Fetching articles by primary category: {}", categoryName);
        return articleRepository.findByPrimaryCategory_Name(categoryName);
    }

    @Override
    public List<Article> findByCategory(String categoryName) {
        log.info("Fetching articles by category: {}", categoryName);
        return articleRepository.findByCategories_Name(categoryName);
    }

    @Override
    public Article findById(Long id) {
        log.info("Fetching article by id: {}", id);
        return articleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("News not found: {}", id);
                    return new RuntimeException("News not found: " + id);
                });
    }

    @Override
    public List<Article> search(String category) {
        log.info("Searching articles with category: {}", category);
        return articleRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(category, category);
    }
}


