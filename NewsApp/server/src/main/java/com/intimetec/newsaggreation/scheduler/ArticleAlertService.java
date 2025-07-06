////package com.intimetec.newsaggreation.scheduler;
////
////import com.intimetec.newsaggreation.model.*;
////import com.intimetec.newsaggreation.repository.*;
////import com.intimetec.newsaggreation.service.EmailService;
////import lombok.RequiredArgsConstructor;
////import lombok.extern.slf4j.Slf4j;
////import org.springframework.scheduling.annotation.Scheduled;
////import org.springframework.stereotype.Component;
////import org.springframework.transaction.annotation.Transactional;
////
////import java.time.LocalDateTime;
////import java.util.List;
////
////@Component
////@RequiredArgsConstructor
////@Slf4j
////public class ArticleAlertJob {
////
////    private final ArticleRepository articleRepo;
////    private final NotificationConfigRepository configRepo;
////    private final NotificationRepository notifyRepo;
////    private final EmailService emailService;
////
////    @Transactional
////    @Scheduled(fixedRate = 1 * 60 * 1000)   // every 5 min
////    public void scan() {
////        System.out.println("hello");
////        LocalDateTime from = LocalDateTime.now().minusMinutes(5);
////        List<Article> recent = articleRepo.findAllByPublishedAtBetween(from, LocalDateTime.now());
////        if (recent.isEmpty()) return;
////
////        List<NotificationConfig> configs = configRepo.findAll();
////
////        for (NotificationConfig cfg : configs) {
////            if (!cfg.isEnabled()) continue;
////            User user = cfg.getUser();
////
////            for (Article art : recent) {
////                if (notifyRepo.existsByUserAndNewsId(user, art.getId())) continue;  // already alerted
////
////                String cat = art.getPrimaryCategory() != null
////                        ? art.getPrimaryCategory().getName().toLowerCase() : "";
////
////                boolean catMatch =
////                        (cfg.isBusiness() && "business".equals(cat)) ||
////                                (cfg.isEntertainment() && "entertainment".equals(cat)) ||
////                                (cfg.isSports() && "sports".equals(cat)) ||
////                                (cfg.isTechnology() && "technology".equals(cat));
////
////                boolean keywordMatch = false;
////                Long matchedKeywordId = null;
////                for (Keyword k : cfg.getKeywords()) {
////                    if (art.getTitle().toLowerCase().contains(k.getTerm().toLowerCase())) {
////                        keywordMatch = true;
////                        matchedKeywordId = k.getId();
////                        break;
////                    }
////                }
////
////                if (catMatch || keywordMatch) {
////
////                    log.info("Mail candidate → {}", user.getEmail());
////                    /* Persist notification */
////                    Notification n = new Notification(
////                            null,
////                            user,
////                            catMatch ? "CATEGORY" : "KEYWORD",
////                            LocalDateTime.now(),
////                            art.getId(),
////                            matchedKeywordId
////                    );
////                    notifyRepo.save(n);
////
////                    /* Send email */
////                    emailService.send(
////                            user.getEmail(),
////                            "News Alert: " + art.getTitle(),
////                            art.getUrl()
////                    );
////                    log.debug("Alert sent to {}", user.getEmail());
////                }
////            }
////        }
////    }
////}
/////////////////////////
////package com.intimetec.newsaggreation.scheduler;
////
////import com.intimetec.newsaggreation.model.*;
////import com.intimetec.newsaggreation.repository.*;
////import com.intimetec.newsaggreation.service.EmailService;
////import lombok.RequiredArgsConstructor;
////import lombok.extern.slf4j.Slf4j;
////import org.springframework.scheduling.annotation.Scheduled;
////import org.springframework.stereotype.Component;
////import org.springframework.transaction.annotation.Transactional;
////
////import java.time.LocalDateTime;
////import java.util.List;
////
////@Component
////@RequiredArgsConstructor
////@Slf4j
////public class ArticleAlertJob {
////
////    private final ArticleRepository articleRepo;
////    private final NotificationConfigRepository configRepo;
////    private final NotificationRepository notifyRepo;
////    private final EmailService emailService;
////
////    @Transactional
////    @Scheduled(fixedRate = 1 * 60 * 1000) // every 1 minute
////    public void scan() {
////        log.info("Starting Article Alert Job...");
////
////        LocalDateTime from = LocalDateTime.now().minusMinutes(5);
////        LocalDateTime to = LocalDateTime.now();
////
////        List<Article> recentArticles = articleRepo.findAllWithCategory(from, to);
////        if (recentArticles.isEmpty()) {
////            log.info("No recent articles found.");
////            return;
////        }
////
////        List<NotificationConfig> configs = configRepo.findAll();
////
////        for (NotificationConfig cfg : configs) {
////            if (!cfg.isEnabled()) continue;
////            System.out.println("hello");
////            User user = cfg.getUser();
////
////            for (Article article : recentArticles) {
////                if (notifyRepo.existsByUserAndNewsId(user, article.getId())) continue;
////
////                String category = article.getPrimaryCategory() != null
////                        ? article.getPrimaryCategory().getName().toLowerCase()
////                        : "";
////
////                boolean catMatch =
////                        (cfg.isBusiness() && "business".equals(category)) ||
////                                (cfg.isEntertainment() && "entertainment".equals(category)) ||
////                                (cfg.isSports() && "sports".equals(category)) ||
////                                (cfg.isTechnology() && "technology".equals(category));
////
////                boolean keywordMatch = false;
////                Long matchedKeywordId = null;
////
////                for (Keyword k : cfg.getKeywords()) {
////                    if (article.getTitle().toLowerCase().contains(k.getTerm().toLowerCase())) {
////                        keywordMatch = true;
////                        matchedKeywordId = k.getId();
////                        break;
////                    }
////                }
////
////                log.debug("→ {}: catMatch={}, keywordMatch={}", user.getEmail(), catMatch, keywordMatch);
////
////                if (catMatch || keywordMatch) {
////                    log.info("Mail candidate → {}", user.getEmail());
////
////                    // Save notification record
////                    Notification n = new Notification(
////                            null,
////                            user,
////                            catMatch ? "CATEGORY" : "KEYWORD",
////                            LocalDateTime.now(),
////                            article.getId(),
////                            matchedKeywordId
////                    );
////                    notifyRepo.save(n);
////
////                    // Send mail
////                    emailService.send(
////                            user.getEmail(),
////                            "News Alert: " + article.getTitle(),
////                            article.getUrl()
////                    );
////                    log.debug("Alert sent to {}", user.getEmail());
////                }
////            }
////        }
////
////        log.info("Article Alert Job completed.");
////    }
////}
//////////////////////
//package com.intimetec.newsaggreation.scheduler;
//
//import com.intimetec.newsaggreation.model.*;
//import com.intimetec.newsaggreation.repository.*;
//import com.intimetec.newsaggreation.service.EmailService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ArticleAlertJob {
//
//    private static final int LOOKBACK_MINUTES = 2400;
//
//    private final ArticleRepository articleRepo;
//    private final NotificationConfigRepository configRepo;
//    private final NotificationRepository notifyRepo;
//    private final EmailService emailService;
//
//    /* ───────────────────────── Scheduler ───────────────────────── */
//
//    @Transactional
//    @Scheduled(fixedRate = 600000_000)
//    public void scan() {
//        log.info("Starting Article Alert Job…");
//
//        List<Article> recent = fetchRecentArticles();
//        if (recent.isEmpty()) {
//            log.info("No recent articles found.");
//            return;
//        }
//
//        List<NotificationConfig> configs = configRepo.findAll();
//        if (configs.isEmpty()) {
//            log.info("No notification configs found, exiting.");
//            return;
//        }
//
//        configs.stream()
//                .filter(NotificationConfig::isEnabled)
//                .forEach(cfg -> processUserConfig(cfg, recent));
//
//        log.info("Article Alert Job completed.");
//    }
//
//    /* ───────────────────────── Helpers ───────────────────────── */
//
//    private List<Article> fetchRecentArticles() {
//        LocalDateTime now   = LocalDateTime.now();
//        LocalDateTime from  = now.minusMinutes(LOOKBACK_MINUTES);
//        return articleRepo.findAllWithCategory(from, now);
//    }
//
//    private void processUserConfig(NotificationConfig cfg, List<Article> recent) {
//        System.out.println("insdieprocessUserConfig");
//        User user = cfg.getUser();
//        Set<String> enabledCats = getEnabledCategories(cfg);
//        Set<String> keywordPool = cfg.getKeywords()
//                .stream()
//                .map(k -> k.getTerm().toLowerCase())
//                .collect(Collectors.toSet());
//
//        for (Article art : recent) {
//
//            if (notifyRepo.existsByUserAndNewsId(user, art.getId())) continue;
//
//            String cat = art.getPrimaryCategory() != null
//                    ? art.getPrimaryCategory().getName().toLowerCase()
//                    : "";
//
//            boolean catMatch = enabledCats.contains(cat);
//            KeywordMatchResult kwResult = findKeywordMatch(art, keywordPool, cfg);
//
//            log.debug("User={} catMatch={} keywordMatch={} art={}",
//                    user.getEmail(), catMatch, kwResult.matched(), art.getTitle());
//
//            if (catMatch || kwResult.matched()) {
//                sendAndPersistNotification(user, art, catMatch, kwResult);
//            }
//        }
//    }
//
//    /* Collect which category flags are ON for this user */
//    private Set<String> getEnabledCategories(NotificationConfig cfg) {
//        Set<String> categories = new HashSet<>();
//        if (cfg.isBusiness()) categories.add("business");
//        if (cfg.isEntertainment()) categories.add("entertainment");
//        if (cfg.isSports()) categories.add("sports");
//        if (cfg.isTechnology()) categories.add("technology");
//        return categories;
//    }
//
//    private KeywordMatchResult findKeywordMatch(Article art,
//                                                Set<String> pool,
//                                                NotificationConfig cfg) {
//
//        String title = art.getTitle().toLowerCase();
//        for (Keyword k : cfg.getKeywords()) {
//            if (title.contains(k.getTerm().toLowerCase())) {
//                return new KeywordMatchResult(true, k.getId());
//            }
//        }
//        return KeywordMatchResult.NO_MATCH;
//    }
//
//    private void sendAndPersistNotification(User user,
//                                            Article art,
//                                            boolean catMatch,
//                                            KeywordMatchResult kwRes) {
//
//        log.info("Sending alert to {}", user.getEmail());
//
//        Notification n = new Notification(
//                null,
//                user,
//                catMatch ? "CATEGORY" : "KEYWORD",
//                LocalDateTime.now(),
//                art.getId(),
//                kwRes.keywordId()
//        );
//        notifyRepo.save(n);
//
//        emailService.send(
//                user.getEmail(),
//                "News Alert: " + art.getTitle(),
//                art.getUrl()
//        );
//    }
//
//    /* ───────────────────────── Records ───────────────────────── */
//
//    private record KeywordMatchResult(boolean matched, Long keywordId) {
//        static final KeywordMatchResult NO_MATCH = new KeywordMatchResult(false, null);
//    }
//}
package com.intimetec.newsaggreation.scheduler;

import com.intimetec.newsaggreation.model.*;
import com.intimetec.newsaggreation.repository.*;
import com.intimetec.newsaggreation.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Full alert logic; all methods ≤ 20 lines and ≤ 3 parameters.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleAlertService {

    private static final int LOOKBACK_MINUTES = 2400;

    private final ArticleRepository articleRepository;
    private final NotificationConfigRepository configRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    /* ─────────────── public entry ─────────────── */

    public void execute() {
        log.info("Starting Article Alert Job…");

        List<Article> recentArticles = loadRecentArticles();
        if (recentArticles.isEmpty()) return;

        fetchEnabledConfigs()
                .forEach(config -> processConfig(config, recentArticles));

        log.info("Article Alert Job completed.");
    }

    /* ─────────────── workflow helpers ─────────────── */

    private List<Article> loadRecentArticles() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusMinutes(LOOKBACK_MINUTES);
        return articleRepository.findAllWithCategory(from, now);
    }

    private List<NotificationConfig> fetchEnabledConfigs() {
        return configRepository.findAll().stream()
                .filter(NotificationConfig::isEnabled)

                .toList();
    }

    private void processConfig(NotificationConfig config, List<Article> articles) {
        User user = config.getUser();
        UserPreferences userPreferences = new UserPreferences(config);

        articles.stream()
                .filter(a -> shouldNotify(user, a, userPreferences))
                .forEach(a -> sendNotification(user, a, userPreferences));
    }

    private boolean shouldNotify(User user, Article article, UserPreferences prefs) {
        if (notificationRepository.existsByUserAndNewsId(user, article.getId())) return false;
        return categoryMatches(article, prefs) || keywordMatch(article, prefs).matched();
    }

    private boolean categoryMatches(Article article, UserPreferences prefs) {
        String categoryName = Optional.ofNullable(article.getPrimaryCategory())
                .map(c -> c.getName().toLowerCase())
                .orElse("");
        return prefs.categories().contains(categoryName);
    }

   private KeywordMatchResult keywordMatch(Article article, UserPreferences prefs) {
        String title = article.getTitle().toLowerCase();
        return prefs.terms().stream()
                .filter(title::contains)
                .findFirst()
                .map(term -> new KeywordMatchResult(true, prefs.idOf(term)))
                .orElse(KeywordMatchResult.NO_MATCH);
    }

    private void sendNotification(User user, Article article, UserPreferences prefs) {
        boolean categoryMatched = categoryMatches(article, prefs);
        KeywordMatchResult keywordMatch = keywordMatch(article, prefs);

        Notification notification = new Notification(
                null, user,
                categoryMatched ? "CATEGORY" : "KEYWORD",
                LocalDateTime.now(),
                article.getId(),
                keywordMatch.keywordId()
        );
        notificationRepository.save(notification);

        emailService.send(
                user.getEmail(),
                "News Alert: " + article.getTitle(),
                article.getUrl()
        );
    }

    /* ─────────────── value objects ─────────────── */

    private record KeywordMatchResult(boolean matched, Long keywordId) {
        static final KeywordMatchResult NO_MATCH = new KeywordMatchResult(false, null);
    }

    /**
     * Holds category set and keyword‑to‑ID map for one user.
     */
    private static class UserPreferences {
        private final Set<String> categories;
        private final Map<String, Long> termToId;

        UserPreferences(NotificationConfig config) {
            categories = extractCategories(config);
            termToId = config.getKeywords().stream()
                    .collect(Collectors.toMap(
                            k -> k.getTerm().toLowerCase(),
                            Keyword::getId));
        }

        private Set<String> extractCategories(NotificationConfig cfg) {
            Set<String> set = new HashSet<>();
            if (cfg.isBusiness()) set.add("business");
            if (cfg.isEntertainment()) set.add("entertainment");
            if (cfg.isSports()) set.add("sports");
            if (cfg.isTechnology()) set.add("technology");
            return set;
        }

        Set<String> categories() {
            return categories;
        }

        Set<String> terms() {
            return termToId.keySet();
        }

        Long idOf(String term) {
            return termToId.get(term);
        }
    }
}
