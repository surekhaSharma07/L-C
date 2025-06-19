package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.SavedArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedArticleRepository extends JpaRepository<SavedArticle, Long> {


}
