package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByUrl(String url);
    List<Article> findAllByPublishedAtBetween(LocalDateTime start, LocalDateTime end);
}

