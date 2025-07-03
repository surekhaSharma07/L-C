// src/main/java/com/intimetec/newsaggreation/controller/UserArticleController.java
package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.ArticleResponse;
import com.intimetec.newsaggreation.model.ReactionType;
import com.intimetec.newsaggreation.service.UserArticleService;
import com.intimetec.newsaggreation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Routes for save / unsave / like / dislike.
 * Security: already covered by your existing filter
 * (matches /api/user/** needs ROLE_USER or ROLE_ADMIN).
 */
@RestController
@RequestMapping("/api/user/articles")
@RequiredArgsConstructor
public class UserArticleController {

    private final UserArticleService service;
    private final UserRepository users;

    /* ----------------------------------------------------------- */
    /* utilities                                                   */
    /* ----------------------------------------------------------- */
    private Long currentUserId(UserDetails principal) {
        return users.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    /* ----------------------------------------------------------- */
    /* 1. list saved                                               */
    /* ----------------------------------------------------------- */
    @GetMapping("/saved")
    public List<ArticleResponse> saved(@AuthenticationPrincipal UserDetails principal) {
        return service.listSavedArticles(currentUserId(principal));
    }

    /* 2. save                                                     */
//    @PostMapping("/save")
//    public ResponseEntity<String> save(@RequestParam Long articleId,
//                                       @AuthenticationPrincipal UserDetails principal) {
//        service.saveArticle(currentUserId(principal), articleId);
//        return ResponseEntity.ok("Article saved");
//    }


    @PostMapping("/save")
    public ResponseEntity<String> save(
            @RequestParam("articleId") Long articleId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        service.saveArticle(currentUserId(principal), articleId);
        return ResponseEntity.ok("Article saved");
    }


    /* 3. unsave                                                   */
//    @DeleteMapping("/save/{articleId}")
//    public ResponseEntity<String> unsave(@PathVariable Long articleId,
//                                         @AuthenticationPrincipal UserDetails principal) {
//        service.unsaveArticle(currentUserId(principal), articleId);
//        return ResponseEntity.ok("Article removed");
//    }

    @DeleteMapping("/save/{articleId}")
    public ResponseEntity<String> unsave(
            @PathVariable("articleId") Long articleId,   // 👈  give Spring the name
            @AuthenticationPrincipal UserDetails principal) {

        service.unsaveArticle(currentUserId(principal), articleId);
        return ResponseEntity.ok("Article removed");
    }

    /* 4. like / dislike                                           */
//    @PutMapping("/reaction")
//    public ResponseEntity<String> react(
//            @RequestParam Long articleId,
//            @RequestParam boolean like,   // true = like, false = dislike
//            @AuthenticationPrincipal UserDetails principal) {
//
//        service.reactToArticle(
//                currentUserId(principal),
//                articleId,
//                like ? ReactionType.LIKE : ReactionType.DISLIKE);
//        return ResponseEntity.ok(like ? "Liked" : "Disliked");
//    }

    @PutMapping("/reaction")
    public ResponseEntity<String> react(
            @RequestParam("articleId") Long articleId,
            @RequestParam("like") boolean like,      // true = like, false = dislike
            @AuthenticationPrincipal UserDetails principal) {

        service.reactToArticle(
                currentUserId(principal),
                articleId,
                like ? ReactionType.LIKE : ReactionType.DISLIKE);

        return ResponseEntity.ok(like ? "Liked" : "Disliked");
    }


}
