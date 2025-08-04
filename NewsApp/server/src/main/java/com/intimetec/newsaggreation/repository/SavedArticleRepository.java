package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.SavedArticle;
import com.intimetec.newsaggreation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedArticleRepository extends JpaRepository<SavedArticle, Long> {

    List<SavedArticle> findByUser(User user);

    void deleteByUserAndNews(User user, Article news);

    Optional<SavedArticle> findByUserAndNews(User user, Article news);
}
