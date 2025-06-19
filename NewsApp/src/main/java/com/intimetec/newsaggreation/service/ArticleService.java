package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.Article;

import java.util.List;

public interface ArticleService {
    boolean existsByUrl(String url);
    Article save(Article news);

    List<Article> findAll();

    List<Article> findToday();

    Article findById(Long id);
}
