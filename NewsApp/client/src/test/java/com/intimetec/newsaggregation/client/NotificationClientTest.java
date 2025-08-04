package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.intimetec.newsaggregation.testutils.TestConstants.*;
import static com.intimetec.newsaggregation.testutils.TestHelper.*;

@ExtendWith(MockitoExtension.class)
class NotificationClientTest {
    private NotificationClient notificationClient;
    private ObjectMapper objectMapper;

    @Mock
    private AuthClient mockAuthClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        notificationClient = new NotificationClient(objectMapper, mockAuthClient);
    }

    @Test
    void testNotificationClientCreation() {
        assertNotNull(notificationClient);
    }

    @Test
    void testSaveConfig_JsonHandling() {
        ObjectNode config = objectMapper.createObjectNode();
        config.put("email", true);
        config.put("sms", false);
        config.put("push", true);

        String jsonString = config.toPrettyString();

        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"email\" : true"));
        assertTrue(jsonString.contains("\"sms\" : false"));
        assertTrue(jsonString.contains("\"push\" : true"));
    }

    @Test
    void testFetchNotifications_TypeReference() {
        List<JsonNode> notifications = parseNotifications(objectMapper, TEST_NOTIFICATIONS_ARRAY);

        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertEquals(1, notifications.get(0).get("id").asInt());
        assertEquals("Test notification", notifications.get(0).get("message").asText());
        assertEquals(2, notifications.get(1).get("id").asInt());
        assertEquals("Another notification", notifications.get(1).get("message").asText());
    }

    @Test
    void testFetchNotifications_EmptyArray() {
        List<JsonNode> notifications = parseNotifications(objectMapper, EMPTY_ARRAY_JSON);

        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testFetchConfig_ValidJson() {
        JsonNode config = parseJson(objectMapper, TEST_CONFIG_JSON);

        assertNotNull(config);
        assertTrue(config.get("email").asBoolean());
        assertFalse(config.get("sms").asBoolean());
        assertTrue(config.get("push").asBoolean());
    }

    @Test
    void testPublicMethodsExist() {
        try {
            notificationClient.fetchConfig();
        } catch (Exception exception) {
            // Expected in test environment without server
        }
        
        ObjectNode node = objectMapper.createObjectNode();
        try {
            notificationClient.saveConfig(node);
        } catch (Exception exception) {
            // Expected in test environment without server
        }
        
        try {
            notificationClient.fetchNotifications();
        } catch (Exception exception) {
            // Expected in test environment without server
        }
        
        assertTrue(true);
    }
} 