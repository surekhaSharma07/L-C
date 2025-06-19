package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.model.Category;
import com.intimetec.newsaggreation.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService svc;

    public CategoryController(CategoryService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Category> all() {
        return svc.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> get(@PathVariable Integer id) {
        return svc.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Category create(@RequestBody Category c) {
        return svc.create(c);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Integer id, @RequestBody Category c) {
        if (!svc.findById(id).isPresent()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(svc.update(id, c));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!svc.findById(id).isPresent()) return ResponseEntity.notFound().build();
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}


