package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Reaction;
import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndNews(User user, Article news);

    List<Reaction> findByUser(User user);

    void deleteByUserAndNews(User user, Article news);
}
