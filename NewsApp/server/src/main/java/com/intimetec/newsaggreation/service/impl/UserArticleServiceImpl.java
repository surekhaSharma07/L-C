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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserArticleServiceImpl implements UserArticleService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final SavedArticleRepository savedArticleRepository;
    private final ReactionRepository reactionRepository;

    @Override
    @Transactional
    public void saveArticle(Long userId, Long articleId) {
        log.info("Saving article for user {} with article {}", userId, articleId);
        User user = fetchUser(userId);
        Article article = fetchArticle(articleId);
        savedArticleRepository.save(new SavedArticle(null, user, article));
    }

    @Override
    @Transactional
    public void unsaveArticle(Long userId, Long articleId) {
        log.info("Unsaving article for user {} with article {}", userId, articleId);
        User user = fetchUser(userId);
        Article article = fetchArticle(articleId);
        savedArticleRepository.deleteByUserAndNews(user, article);
    }

    @Override
    public List<ArticleResponse> listSavedArticles(Long userId) {
        log.info("Listing saved articles for user {}", userId);
        User user = fetchUser(userId);
        return savedArticleRepository.findAll().stream()
                .filter(savedArticle -> savedArticle.getUser().equals(user))
                .map(savedArticle -> ArticleResponse.from(savedArticle.getNews()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void reactToArticle(Long userId, Long articleId, ReactionType type) {
        log.info("React to article for user {} with article {} and type {}", userId, articleId, type);
        User user = fetchUser(userId);
        Article article = fetchArticle(articleId);

        reactionRepository.findByUserAndNews(user, article)
                .ifPresentOrElse(
                        reaction -> reaction.setType(type),
                        () -> reactionRepository.save(
                                new Reaction(null, user, article, type))
                );
    }

    @Override
    @Transactional
    public void removeReaction(Long userId, Long articleId) {
        log.info("Remove reaction for user {} with article {}", userId, articleId);
        User user = fetchUser(userId);
        Article article = fetchArticle(articleId);
        reactionRepository.deleteByUserAndNews(user, article);
    }

    private User fetchUser(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found: " + userId);
                });
    }

    private Article fetchArticle(Long articleId) {
        log.info("Fetching article with ID: {}", articleId);
        return articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    log.error("Article not found with ID: {}", articleId);
                    return new RuntimeException("Article not found: " + articleId);
                });
    }
}
