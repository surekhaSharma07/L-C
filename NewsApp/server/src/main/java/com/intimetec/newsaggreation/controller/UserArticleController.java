package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.ArticleResponse;
import com.intimetec.newsaggreation.model.ReactionType;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.service.ArticleReportingService;
import com.intimetec.newsaggreation.service.UserArticleService;
import com.intimetec.newsaggreation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/articles")
@RequiredArgsConstructor
public class UserArticleController {

    private final UserArticleService service;
    private final UserRepository users;
    private final ArticleReportingService articleReportingService;

    private Long currentUserId(UserDetails principal) {
        return users.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping("/saved")
    public List<ArticleResponse> saved(@AuthenticationPrincipal UserDetails principal) {
        return service.listSavedArticles(currentUserId(principal));
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(
            @RequestParam("articleId") Long articleId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        service.saveArticle(currentUserId(principal), articleId);
        return ResponseEntity.ok("Article saved");
    }

    @DeleteMapping("/save/{articleId}")
    public ResponseEntity<String> unsave(
            @PathVariable("articleId") Long articleId,
            @AuthenticationPrincipal UserDetails principal) {

        service.unsaveArticle(currentUserId(principal), articleId);
        return ResponseEntity.ok("Article removed");
    }

    @PutMapping("/reaction")
    public ResponseEntity<String> react(
            @RequestParam("articleId") Long articleId,
            @RequestParam("like") boolean like,
            @AuthenticationPrincipal UserDetails principal) {

        service.reactToArticle(
                currentUserId(principal),
                articleId,
                like ? ReactionType.LIKE : ReactionType.DISLIKE);

        return ResponseEntity.ok(like ? "Liked" : "Disliked");
    }

    @PostMapping("/report")
    public ResponseEntity<String> reportArticle(@RequestParam("articleId") Long articleId,
                                                @RequestParam("reason") String reason,
                                                @AuthenticationPrincipal UserDetails principal) {

        User user = users.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        articleReportingService.submitReport(user, articleId, reason);
        return ResponseEntity.ok("Report submitted");
    }
}
