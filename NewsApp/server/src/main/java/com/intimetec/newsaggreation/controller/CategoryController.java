package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.model.Category;
import com.intimetec.newsaggreation.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> all() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public Category one(@PathVariable Integer id) {
        return categoryService.findById(id);
    }

    @PostMapping
    public Category create(@RequestBody Category cat) {
        return categoryService.create(cat);
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable Integer id,
                           @RequestBody Category cat) {
        return categoryService.update(id, cat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Category addCategoryAdmin(@Valid @RequestBody Map<String, String> body) {
        String name = body.get("name");
        Category category = new Category();
        category.setName(name);
        return categoryService.create(category);
    }
}
