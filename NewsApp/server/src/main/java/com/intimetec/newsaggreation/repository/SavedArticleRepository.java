package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.SavedArticle;
import com.intimetec.newsaggreation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedArticleRepository extends JpaRepository<SavedArticle, Long> {

    /**
     * all saves of one user
     */
    List<SavedArticle> findByUser(User user);

    /**
     * delete one specific record
     */
    void deleteByUserAndNews(User user, Article news);

    /**
     * optional helper for “already saved?” checks
     */
    Optional<SavedArticle> findByUserAndNews(User user, Article news);
}
