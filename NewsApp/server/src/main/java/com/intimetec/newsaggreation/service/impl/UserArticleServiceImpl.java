package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.model.Reaction;
import com.intimetec.newsaggreation.model.ReactionType;
import com.intimetec.newsaggreation.repository.ReactionRepository;
import com.intimetec.newsaggreation.service.UserArticleService;
import com.intimetec.newsaggreation.dto.ArticleResponse;
import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.SavedArticle;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.ArticleRepository;
import com.intimetec.newsaggreation.repository.SavedArticleRepository;
import com.intimetec.newsaggreation.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserArticleServiceImpl implements UserArticleService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final SavedArticleRepository savedArticleRepository;
    private final ReactionRepository reactionRepository;

    @Override
    @Transactional
    public void saveArticle(Long userId, Long articleId) {
        User user = fetchUser(userId);
        Article article = fetchArticle(articleId);
        savedArticleRepository.save(new SavedArticle(null, user, article));
    }

    @Override
    @Transactional
    public void unsaveArticle(Long userId, Long articleId) {
        User user = fetchUser(userId);
        Article article = fetchArticle(articleId);
        savedArticleRepository.deleteByUserAndNews(user, article);
    }

    @Override
    public List<ArticleResponse> listSavedArticles(Long userId) {
        User user = fetchUser(userId);
        return savedArticleRepository.findAll().stream()
                .filter(s -> s.getUser().equals(user))
                .map(s -> ArticleResponse.from(s.getNews()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void reactToArticle(Long userId, Long articleId, ReactionType type) {
        User user = fetchUser(userId);
        Article article = fetchArticle(articleId);

        reactionRepository.findByUserAndNews(user, article)
                .ifPresentOrElse(
                        r -> r.setType(type),
                        () -> reactionRepository.save(
                                new Reaction(null, user, article, type))
                );
    }


    @Override
    @Transactional
    public void removeReaction(Long userId, Long articleId) {
        User user = fetchUser(userId);
        Article article = fetchArticle(articleId);
        reactionRepository.deleteByUserAndNews(user, article);
    }

    private User fetchUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }

    private Article fetchArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));
    }
}
