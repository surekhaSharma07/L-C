package com.intimetec.newsaggreation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        log.info("HTTP client is ready to make server communication");
        return new RestTemplate();
    }
}

