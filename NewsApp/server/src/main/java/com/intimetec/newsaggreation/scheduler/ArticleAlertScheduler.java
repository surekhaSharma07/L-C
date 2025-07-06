package com.intimetec.newsaggreation.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Timer‑only orchestrator — delegates work to ArticleAlertService.
 */
@Component
@RequiredArgsConstructor
public class ArticleAlertScheduler {

    private final ArticleAlertService alertService;

    @Transactional
    @Scheduled(fixedRate = 600000_000)
    public void runAlertJob() {
        alertService.execute();
    }
}
