package com.intimetec.newsaggreation.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleAlertScheduler {

    private final ArticleAlertService alertService;

    @Transactional
    @Scheduled(fixedRate = 600000_000)
    public void runAlertJob() {
        log.info("Running scheduled ArticleAlert job");
        alertService.execute();
    }
}
