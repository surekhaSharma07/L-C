//package com.intimetec.newsaggreation.service;
//
//import com.intimetec.newsaggreation.model.Article;
//
//import java.util.List;
//
//public interface ArticleService {
//    boolean existsByUrl(String url);
//    Article save(Article news);
//
//    List<Article> findAll();
//
//    List<Article> findToday();
//
//    Article findById(Long id);
//}
package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.Article;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

public interface ArticleService {
    boolean existsByUrl(String url);
    Article save(Article news);
    List<Article> findAll();
    List<Article> findToday();
    Article findById(Long id);

    List<Article> findByPrimaryCategory(String categoryName);

    List<Article> findByCategory(String categoryName);

    List<Article> findByDateAndCategory(LocalDate date, String category);

    List<Article> findByDateRangeAndCategory(LocalDate from, LocalDate to, String category);

    List<Article> findByDateRange(LocalDate from, LocalDate to);
}
