package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.model.Category;
import com.intimetec.newsaggreation.repository.CategoryRepository;
import com.intimetec.newsaggreation.service.CategoryService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        log.info("Fetching all categories");
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Integer id) {
        log.info("Fetching category by id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category not found: {}", id);
                    return new RuntimeException("Category not found: " + id);
                });
    }

    @Override
    public Category create(Category category) {
        log.info("Creating category: {}", category.getName());
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Integer id, Category category) {
        log.info("Updating category with id: {}", id);
        Category existing = findById(id);
        existing.setName(category.getName());
        return categoryRepository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting category with id: {}", id);
        Category existing = findById(id);
        categoryRepository.delete(existing);
    }

    @Override
    public Category findOrCreate(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    log.info("Creating new category: {}", name);
                    return categoryRepository.save(category);
                });
    }
}
