package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.Category;
import java.util.List;

public interface CategoryService {
    /** List every category */
    List<Category> findAll();

    /** Fetch one by its ID (or throw) */
    Category findById(Integer id);

    /** Create a new category */
    Category create(Category category);

    /** Update an existing category */
    Category update(Integer id, Category category);

    /** Delete by ID */
    void delete(Integer id);

    /** Existing helper used by your scheduler */
    Category findOrCreate(String name);
}
