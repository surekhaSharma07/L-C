//package com.intimetec.newsaggreation.repository;
//
//import com.intimetec.newsaggreation.model.Article;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public interface ArticleRepository extends JpaRepository<Article, Long> {
//    boolean existsByUrl(String url);
//    List<Article> findAllByPublishedAtBetween(LocalDateTime start, LocalDateTime end);
//}
///////
package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    boolean existsByUrl(String url);

    List<Article> findAllByPublishedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Article> findByPrimaryCategory_Name(String categoryName);

    List<Article> findByCategories_Name(String categoryName);

    List<Article> findAllByPublishedAtBetweenAndPrimaryCategory_Name(
            LocalDateTime start,
            LocalDateTime end,
            String categoryName
    );

    List<Article> findAllByPublishedAtBetweenAndCategories_Name(
            LocalDateTime start,
            LocalDateTime end,
            String categoryName
    );

}


