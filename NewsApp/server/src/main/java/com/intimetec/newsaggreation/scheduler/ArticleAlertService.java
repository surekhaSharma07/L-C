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

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleAlertService {

    private static final int LOOKBACK_MINUTES = 2400;

    private final ArticleRepository articleRepository;
    private final NotificationConfigRepository configRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;


    public void execute() {
        log.info("Starting Article Alert Job…");

        List<Article> recentArticles = loadRecentArticles();
        if (recentArticles.isEmpty()) {
            log.info("No recent articles found for alerting.");
            return;
        }

        List<NotificationConfig> configs = fetchEnabledConfigs();
        log.info("Found {} enabled notification configs.", configs.size());

        configs.forEach(config -> processConfig(config, recentArticles));

        log.info("Article Alert Job completed.");
    }


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
        log.info("Processing notification config for user: {}", user.getEmail());

        articles.stream()
                .filter(a -> shouldNotify(user, a, userPreferences))
                .forEach(a -> {
                    log.info("User {} will be notified for article {}.", user.getEmail(), a.getId());
                    sendNotification(user, a, userPreferences);
                });
    }

    private boolean shouldNotify(User user, Article article, UserPreferences prefs) {
        if (notificationRepository.existsByUserAndNewsId(user, article.getId())) {
            log.debug("User {} already notified for article {}.", user.getEmail(), article.getId());
            return false;
        }
        boolean catMatch = categoryMatches(article, prefs);
        boolean kwMatch = keywordMatch(article, prefs).matched();
        if (catMatch || kwMatch) {
            log.debug("User {} matches article {}: categoryMatch={}, keywordMatch={}", user.getEmail(), article.getId(), catMatch, kwMatch);
        }
        return catMatch || kwMatch;
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
        log.debug("Alert sent to {} for article {}", user.getEmail(), article.getId());
    }

    private record KeywordMatchResult(boolean matched, Long keywordId) {
        static final KeywordMatchResult NO_MATCH = new KeywordMatchResult(false, null);
    }

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
