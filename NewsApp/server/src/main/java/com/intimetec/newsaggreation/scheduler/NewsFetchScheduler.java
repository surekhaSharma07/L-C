package com.intimetec.newsaggreation.scheduler;

import com.intimetec.newsaggreation.repository.ApiSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NewsFetchScheduler {

    private final ApiSourceRepository apiSourceRepo;
    private final NewsFetchService newsFetchService;

    public NewsFetchScheduler(ApiSourceRepository apiSourceRepo,
                              NewsFetchService newsFetchService) {
        this.apiSourceRepo = apiSourceRepo;
        this.newsFetchService = newsFetchService;
    }

    @Scheduled(fixedRateString = "${app.fetch-rate-ms}")
    public void runScheduledFetch() {
        log.info("Running scheduled NewsFetch job");
        apiSourceRepo.findAll().forEach(newsFetchService::processSource);
    }
}
