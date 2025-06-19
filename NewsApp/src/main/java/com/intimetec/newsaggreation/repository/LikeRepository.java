package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long>
{
}
