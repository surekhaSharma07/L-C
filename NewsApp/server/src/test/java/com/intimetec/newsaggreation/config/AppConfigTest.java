package com.intimetec.newsaggreation.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AppConfigTest {

    private AppConfig appConfig = new AppConfig();

    @Test
    void testRestTemplateBean() {
        RestTemplate restTemplate = appConfig.restTemplate();

        assertNotNull(restTemplate);
        assertTrue(restTemplate instanceof RestTemplate);
    }

    @Test
    void testRestTemplateBeanIsNotNull() {
        RestTemplate restTemplate = appConfig.restTemplate();

        assertNotNull(restTemplate, "RestTemplate should not be null");
    }

    @Test
    void testRestTemplateBeanIsNewInstance() {
        RestTemplate firstRestTemplate = appConfig.restTemplate();
        RestTemplate secondRestTemplate = appConfig.restTemplate();

        assertNotNull(firstRestTemplate);
        assertNotNull(secondRestTemplate);
        assertNotSame(firstRestTemplate, secondRestTemplate);
    }

    @Test
    void testRestTemplateBeanIsConfigurable() {
        RestTemplate restTemplate = appConfig.restTemplate();

        assertNotNull(restTemplate);
        assertDoesNotThrow(() -> {
            restTemplate.getInterceptors();
            restTemplate.getRequestFactory();
        });
    }
} 