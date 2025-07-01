package com.intimetec.newsaggreation.scheduler;

import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.NotificationConfig;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.ArticleRepository;
import com.intimetec.newsaggreation.repository.NotificationConfigRepository;
import com.intimetec.newsaggreation.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleAlertJob {

    private final ArticleRepository articleRepo;
    private final NotificationConfigRepository configRepo;
    private final EmailService emailService;

    @Scheduled(fixedRate = 5 * 60 * 1000) // every 5 minutes
    public void checkAndNotify() {
        log.info("Running ArticleNotificationJob...");

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Article> recentArticles = articleRepo.findAllByPublishedAtBetween(fiveMinutesAgo, LocalDateTime.now());

        List<NotificationConfig> configs = configRepo.findAll();

        for (NotificationConfig config : configs) {
            if (!config.isEnabled()) continue;

            User user = config.getUser();

            for (Article article : recentArticles) {
                String category = article.getPrimaryCategory() != null
                        ? article.getPrimaryCategory().getName().toLowerCase()
                        : "";

                boolean categoryMatch =
                        (config.isBusiness() && "business".equals(category)) ||
                                (config.isEntertainment() && "entertainment".equals(category)) ||
                                (config.isSports() && "sports".equals(category)) ||
                                (config.isTechnology() && "technology".equals(category));

                boolean keywordMatch = config.getKeywords().stream()
                        .anyMatch(k -> article.getTitle().toLowerCase().contains(k.getTerm().toLowerCase()));

                if (categoryMatch || keywordMatch) {
                    emailService.send(
                            user.getEmail(),
                            "News Alert: " + article.getTitle(),
                            article.getUrl()
                    );
                }
            }
        }

        log.info("ArticleNotificationJob completed.");
    }
}
