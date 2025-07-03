package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.ReactionType;
import com.intimetec.newsaggreation.dto.ArticleResponse;

import java.util.List;

public interface UserArticleService {
    void saveArticle(Long userId, Long articleId);

    void unsaveArticle(Long userId, Long articleId);

    List<ArticleResponse> listSavedArticles(Long userId);

    void reactToArticle(Long userId, Long articleId, ReactionType type);

    void removeReaction(Long userId, Long articleId);
}