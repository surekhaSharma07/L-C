package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
