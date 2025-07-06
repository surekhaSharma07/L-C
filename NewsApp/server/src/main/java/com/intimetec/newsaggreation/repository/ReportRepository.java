// src/main/java/com/intimetec/newsaggreation/repository/ReportRepository.java
package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.Report;
import com.intimetec.newsaggreation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByUserAndArticle(User user, Article article);

    int countByArticle(Article article);

    List<Report> findByArticle_Id(Long articleId);

}
