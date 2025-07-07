package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.BlockedKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedKeywordRepository extends JpaRepository<BlockedKeyword, Long> {
    boolean existsByTermIgnoreCase(String term);
}
