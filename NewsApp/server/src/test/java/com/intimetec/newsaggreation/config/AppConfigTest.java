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
        // Act
        RestTemplate restTemplate = appConfig.restTemplate();

        // Assert
        assertNotNull(restTemplate);
        assertTrue(restTemplate instanceof RestTemplate);
    }

    @Test
    void testRestTemplateBeanIsNotNull() {
        // Act
        RestTemplate restTemplate = appConfig.restTemplate();

        // Assert
        assertNotNull(restTemplate, "RestTemplate should not be null");
    }

    @Test
    void testRestTemplateBeanIsNewInstance() {
        // Act
        RestTemplate restTemplate1 = appConfig.restTemplate();
        RestTemplate restTemplate2 = appConfig.restTemplate();

        // Assert
        assertNotNull(restTemplate1);
        assertNotNull(restTemplate2);
        // Each call should return a new instance (not singleton)
        assertNotSame(restTemplate1, restTemplate2);
    }

    @Test
    void testRestTemplateBeanIsConfigurable() {
        // Act
        RestTemplate restTemplate = appConfig.restTemplate();

        // Assert
        assertNotNull(restTemplate);
        // Test that the RestTemplate can be configured (no exceptions thrown)
        assertDoesNotThrow(() -> {
            restTemplate.getInterceptors();
            restTemplate.getRequestFactory();
        });
    }
} 