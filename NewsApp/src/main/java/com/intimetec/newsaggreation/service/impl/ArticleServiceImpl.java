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

    private final ArticleRepository articleRepository;

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
        return repo.findAllByPublishedAtBetween(start, end);
    }

    @Override
    public Article findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found: " + id));
    }
}
