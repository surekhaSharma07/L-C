package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class ArticleController {

    private final ArticleService service;

    public ArticleController(ArticleService service) {
        this.service = service;
    }

    @GetMapping
    public List<Article> all() {
        return service.findAll();
    }

    @GetMapping("/today")
    public List<Article> today() {
        return service.findToday();
    }

    @GetMapping("/{id}")
    public Article ArticleController(@PathVariable Long id) {
        return service.findById(id);
    }
}
