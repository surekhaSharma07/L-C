package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category, Integer> {}
