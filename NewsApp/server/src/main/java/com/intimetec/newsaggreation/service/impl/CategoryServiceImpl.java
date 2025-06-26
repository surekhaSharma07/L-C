package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.model.Category;
import com.intimetec.newsaggreation.repository.CategoryRepository;
import com.intimetec.newsaggreation.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;

    public CategoryServiceImpl(CategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Category> findAll() {
        return repo.findAll();
    }

    @Override
    public Category findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    @Override
    public Category create(Category category) {
        return repo.save(category);
    }

    @Override
    public Category update(Integer id, Category category) {
        Category existing = findById(id);
        existing.setName(category.getName());
        return repo.save(existing);
    }

    @Override
    public void delete(Integer id) {
        Category existing = findById(id);
        repo.delete(existing);
    }

    @Override
    public Category findOrCreate(String name) {
        return repo.findByName(name)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    return repo.save(c);
                });
    }
}
