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
        // Act
        NotificationConfig config = NotificationConfigFactory.createDefault(testUser);

        // Assert
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
        // Act
        NotificationConfig config = NotificationConfigFactory.createDefault(null);

        // Assert
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
        // Arrange
        User userWithAllProperties = new User();
        userWithAllProperties.setId(999L);
        userWithAllProperties.setEmail("complex@example.com");
        userWithAllProperties.setPasswordHash("hashedPassword");

        // Act
        NotificationConfig config = NotificationConfigFactory.createDefault(userWithAllProperties);

        // Assert
        assertNotNull(config);
        assertEquals(userWithAllProperties, config.getUser());
        assertEquals(999L, config.getUser().getId());
        assertEquals("complex@example.com", config.getUser().getEmail());
    }

    @Test
    void testCreateDefault_DefaultValuesAreCorrect() {
        // Act
        NotificationConfig config = NotificationConfigFactory.createDefault(testUser);

        // Assert - Verify all boolean flags are false by default
        assertFalse(config.isBusiness(), "Business should be false by default");
        assertFalse(config.isEntertainment(), "Entertainment should be false by default");
        assertFalse(config.isSports(), "Sports should be false by default");
        assertFalse(config.isTechnology(), "Technology should be false by default");
        assertTrue(config.isEnabled(), "Enabled should be true by default");
    }

    @Test
    void testCreateDefault_ChannelIsEmail() {
        // Act
        NotificationConfig config = NotificationConfigFactory.createDefault(testUser);

        // Assert
        assertEquals("email", config.getChannel(), "Channel should be 'email' by default");
    }

    @Test
    void testCreateDefault_KeywordsSetIsEmpty() {
        // Act
        NotificationConfig config = NotificationConfigFactory.createDefault(testUser);

        // Assert
        assertNotNull(config.getKeywords(), "Keywords set should not be null");
        assertTrue(config.getKeywords().isEmpty(), "Keywords set should be empty by default");
    }

    @Test
    void testCreateDefault_MultipleCallsCreateDifferentInstances() {
        // Act
        NotificationConfig config1 = NotificationConfigFactory.createDefault(testUser);
        NotificationConfig config2 = NotificationConfigFactory.createDefault(testUser);

        // Assert
        assertNotSame(config1, config2, "Each call should create a new instance");
        assertEquals(config1.getUser(), config2.getUser(), "Both should reference the same user");
        assertEquals(config1.isBusiness(), config2.isBusiness(), "Both should have same business setting");
        assertEquals(config1.isEntertainment(), config2.isEntertainment(), "Both should have same entertainment setting");
        assertEquals(config1.isSports(), config2.isSports(), "Both should have same sports setting");
        assertEquals(config1.isTechnology(), config2.isTechnology(), "Both should have same technology setting");
        assertEquals(config1.isEnabled(), config2.isEnabled(), "Both should have same enabled setting");
        assertEquals(config1.getChannel(), config2.getChannel(), "Both should have same channel");
    }
} 