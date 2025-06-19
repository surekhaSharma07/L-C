package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.Category;
import java.util.List;
import java.util.Optional;
public interface CategoryService {
    List<Category> findAll();
    Optional<Category> findById(Integer id);
    Category create(Category category);
    Category update(Integer id, Category category);
    void delete(Integer id);
}

