package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    Category findById(Integer id);

    Category create(Category category);

    Category update(Integer id, Category category);

    void delete(Integer id);

    Category findOrCreate(String name);
}
