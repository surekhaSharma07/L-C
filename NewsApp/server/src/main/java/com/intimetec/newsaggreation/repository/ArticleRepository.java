package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface
ArticleRepository extends JpaRepository<Article, Long> {

    boolean existsByUrl(String url);

    List<Article> findAllByPublishedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Article> findByPrimaryCategory_Name(String categoryName);

    List<Article> findByCategories_Name(String categoryName);

    List<Article> findAllByPublishedAtBetweenAndPrimaryCategory_Name(
            LocalDateTime start,
            LocalDateTime end,
            String categoryName
    );

    List<Article> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String titlePart, String descriptionPart
    );

    List<Article> findAllByPublishedAtBetweenAndCategories_Name(
            LocalDateTime start,
            LocalDateTime end,
            String categoryName
    );

    List<Article> findByPrimaryCategory_Id(Integer categoryId);

    @Query("""
            SELECT a
            FROM   Article a
            JOIN   FETCH a.primaryCategory
            WHERE  a.publishedAt BETWEEN :from AND :to
            """)
    List<Article> findAllWithCategory(@Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to);
}


