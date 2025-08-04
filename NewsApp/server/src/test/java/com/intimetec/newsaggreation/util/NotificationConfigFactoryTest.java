package com.intimetec.newsaggreation.util;

import com.intimetec.newsaggreation.model.NotificationConfig;
import com.intimetec.newsaggreation.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationConfigFactoryTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    void testCreateDefault_WithValidUser() {
        NotificationConfig config = NotificationConfigFactory.createDefault(testUser);

        assertNotNull(config);
        assertEquals(testUser, config.getUser());
        assertFalse(config.isBusiness());
        assertFalse(config.isEntertainment());
        assertFalse(config.isSports());
        assertFalse(config.isTechnology());
        assertTrue(config.isEnabled());
        assertEquals("email", config.getChannel());
        assertNotNull(config.getKeywords());
        assertTrue(config.getKeywords().isEmpty());
    }

    @Test
    void testCreateDefault_WithNullUser() {
        NotificationConfig config = NotificationConfigFactory.createDefault(null);

        assertNotNull(config);
        assertNull(config.getUser());
        assertFalse(config.isBusiness());
        assertFalse(config.isEntertainment());
        assertFalse(config.isSports());
        assertFalse(config.isTechnology());
        assertTrue(config.isEnabled());
        assertEquals("email", config.getChannel());
        assertNotNull(config.getKeywords());
        assertTrue(config.getKeywords().isEmpty());
    }

    @Test
    void testCreateDefault_UserPropertiesAreSet() {
        User userWithAllProperties = new User();
        userWithAllProperties.setId(999L);
        userWithAllProperties.setEmail("complex@example.com");
        userWithAllProperties.setPasswordHash("hashedPassword");

        NotificationConfig config = NotificationConfigFactory.createDefault(userWithAllProperties);

        assertNotNull(config);
        assertEquals(userWithAllProperties, config.getUser());
        assertEquals(999L, config.getUser().getId());
        assertEquals("complex@example.com", config.getUser().getEmail());
    }

    @Test
    void testCreateDefault_DefaultValuesAreCorrect() {
        NotificationConfig config = NotificationConfigFactory.createDefault(testUser);

        assertFalse(config.isBusiness());
        assertFalse(config.isEntertainment());
        assertFalse(config.isSports());
        assertFalse(config.isTechnology());
        assertTrue(config.isEnabled());
    }

    @Test
    void testCreateDefault_ChannelIsEmail() {
        NotificationConfig config = NotificationConfigFactory.createDefault(testUser);

        assertEquals("email", config.getChannel());
    }

    @Test
    void testCreateDefault_KeywordsSetIsEmpty() {
        NotificationConfig config = NotificationConfigFactory.createDefault(testUser);

        assertNotNull(config.getKeywords());
        assertTrue(config.getKeywords().isEmpty());
    }

    @Test
    void testCreateDefault_MultipleCallsCreateDifferentInstances() {
        NotificationConfig FirstNotificationConfig = NotificationConfigFactory.createDefault(testUser);
        NotificationConfig secondNotificationconfig = NotificationConfigFactory.createDefault(testUser);

        assertNotSame(FirstNotificationConfig, secondNotificationconfig);
        assertEquals(FirstNotificationConfig.getUser(), secondNotificationconfig.getUser());
        assertEquals(FirstNotificationConfig.isBusiness(), secondNotificationconfig.isBusiness());
        assertEquals(FirstNotificationConfig.isEntertainment(), secondNotificationconfig.isEntertainment());
        assertEquals(FirstNotificationConfig.isSports(), secondNotificationconfig.isSports());
        assertEquals(FirstNotificationConfig.isTechnology(), secondNotificationconfig.isTechnology());
        assertEquals(FirstNotificationConfig.isEnabled(), secondNotificationconfig.isEnabled());
        assertEquals(FirstNotificationConfig.getChannel(), secondNotificationconfig.getChannel());
    }
} 