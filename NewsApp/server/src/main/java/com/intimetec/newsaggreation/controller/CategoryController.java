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
    private final CategoryService svc;

    public CategoryController(CategoryService svc) {
        this.svc = svc;
    }

    /**
     * GET /api/categories
     */
    @GetMapping
    public List<Category> all() {
        return svc.findAll();
    }

    /**
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public Category one(@PathVariable Integer id) {
        return svc.findById(id);
    }

    /**
     * POST /api/categories
     */
    @PostMapping
    public Category create(@RequestBody Category cat) {
        return svc.create(cat);
    }

    /**
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public Category update(@PathVariable Integer id,
                           @RequestBody Category cat) {
        return svc.update(id, cat);
    }

    /**
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    // POST  /api/categories/admin   (only admins allowed)
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Category addCategoryAdmin(@Valid @RequestBody Map<String, String> body) {
        String name = body.get("name");
        Category cat = new Category();
        cat.setName(name);
        return svc.create(cat);          // reuse your service layer
    }


}
