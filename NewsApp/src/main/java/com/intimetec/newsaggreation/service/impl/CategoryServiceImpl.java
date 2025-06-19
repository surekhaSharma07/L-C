package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.model.Category;
import com.intimetec.newsaggreation.repository.CategoryRepository;
import com.intimetec.newsaggreation.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List; import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;
    public CategoryServiceImpl(CategoryRepository repo) { this.repo = repo; }
    public List<Category> findAll() { return repo.findAll(); }
    public Optional<Category> findById(Integer id) { return repo.findById(id); }
    public Category create(Category c) { return repo.save(c); }
    public Category update(Integer id, Category c) { c.setId(id); return repo.save(c); }
    public void delete(Integer id) { repo.deleteById(id); }
}
