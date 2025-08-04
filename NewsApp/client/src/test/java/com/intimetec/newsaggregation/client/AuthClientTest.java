package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.*;
import static com.intimetec.newsaggregation.testutils.TestConstants.*;
import static com.intimetec.newsaggregation.testutils.TestHelper.*;

class AuthClientTest {

    private AuthClient authClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        authClient = new AuthClient(objectMapper);
    }

    @Test
    void testAuthClientCreation() {
        AuthClient client = new AuthClient(objectMapper);
        assertNotNull(client);
    }

    @Test
    void testGetJwtToken_InitiallyNull() {
        String token = authClient.getJwtToken();
        assertNull(token);
    }

    @Test
    void testGetRole_InitiallyNull() {
        String role = authClient.getRole();
        assertNull(role);
    }

    @Test
    void testCreateAuthRequestBody_ValidInput() throws Exception {
        String requestBody = createAuthRequestBody(authClient, TEST_EMAIL, TEST_PASSWORD);

        assertNotNull(requestBody);
        assertTrue(requestBody.contains(TEST_EMAIL));
        assertTrue(requestBody.contains(TEST_PASSWORD));
        
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        assertEquals(TEST_EMAIL, jsonNode.get("email").asText());
        assertEquals(TEST_PASSWORD, jsonNode.get("password").asText());
    }

    @Test
    void testCreatePostRequest_ValidUrl() {
        String url = API_BASE_URL + "/auth/signup";
        String body = "{\"email\":\"" + TEST_EMAIL + "\",\"password\":\"" + TEST_PASSWORD + "\"}";

        HttpRequest request = createPostRequest(authClient, url, body);

        assertNotNull(request);
        assertEquals(POST_METHOD, request.method());
        assertTrue(request.headers().firstValue(CONTENT_TYPE_HEADER).isPresent());
        assertEquals(APPLICATION_JSON, request.headers().firstValue(CONTENT_TYPE_HEADER).get());
    }
} 