package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.ArticleResponse;
import com.intimetec.newsaggreation.service.ArticleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@Slf4j
public class ArticleController {
    private final ArticleService service;

    public ArticleController(ArticleService service) {
        this.service = service;
    }

    @GetMapping
    public List<ArticleResponse> all() {
        log.info("GET /api/news");
        return service.findAll().stream()
                .map(ArticleResponse::from)
                .toList();
    }

    @GetMapping(params = {"date", "category"})
    public List<ArticleResponse> byDateAndCategory(
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "category") String category
    ) {
        log.info("GET /api/news?date={}&category={}", date, category);
        return service.findByDateAndCategory(date, category).stream()
                .map(ArticleResponse::from)
                .toList();
    }

    @GetMapping(params = {"from", "to"})
    public List<ArticleResponse> byDateRange(
            @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        log.info("GET /api/news?from={}&to={}", from, to);
        return service.findByDateRange(from, to).stream()
                .map(ArticleResponse::from)
                .toList();
    }

    @GetMapping(params = {"from", "to", "category"})
    public List<ArticleResponse> byDateRangeAndCategory(
            @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(name = "category") String category
    ) {
        log.info("GET /api/news?from={}&to={}&category={}", from, to, category);
        return service.findByDateRangeAndCategory(from, to, category).stream()
                .map(ArticleResponse::from)
                .toList();
    }

    @GetMapping("/today")
    public List<ArticleResponse> today() {
        log.info("GET /api/news/today");
        return service.findToday().stream()
                .map(ArticleResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ArticleResponse byId(@PathVariable Long id) {
        log.info("GET /api/news/{}", id);
        return ArticleResponse.from(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArticleResponse>> searchArticles(
            @RequestParam("query") String query,
            @AuthenticationPrincipal UserDetails principal) {
        log.info("GET /api/news/search?query={}", query);
        List<ArticleResponse> result = service.search(query)
                .stream()
                .map(ArticleResponse::from)
                .toList();

        return ResponseEntity.ok(result);
    }
}
