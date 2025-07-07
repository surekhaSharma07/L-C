package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.http.HttpRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static com.intimetec.newsaggregation.testutils.TestConstants.*;
import static com.intimetec.newsaggregation.testutils.TestHelper.*;

@ExtendWith(MockitoExtension.class)
class UserArticleClientTest {
    private UserArticleClient userArticleClient;
    private ObjectMapper objectMapper;

    @Mock
    private AuthClient mockAuthClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userArticleClient = new UserArticleClient(mockAuthClient, objectMapper);
    }

    @Test
    void testUserArticleClientCreation() {
        assertNotNull(userArticleClient);
    }

    @Test
    void testCreateHttpRequest_WithBody() {
        when(mockAuthClient.getJwtToken()).thenReturn(TEST_JWT_TOKEN);
        String method = POST_METHOD;
        String path = "/save?articleId=123";
        String body = "{\"test\":\"data\"}";

        HttpRequest request = createHttpRequest(userArticleClient, method, path, body);

        assertNotNull(request);
        assertEquals(POST_METHOD, request.method());
        assertTrue(request.headers().firstValue(AUTHORIZATION_HEADER).isPresent());
        assertTrue(request.headers().firstValue(ACCEPT_HEADER).isPresent());
        assertTrue(request.headers().firstValue(CONTENT_TYPE_HEADER).isPresent());
        assertEquals(BEARER_PREFIX + TEST_JWT_TOKEN, request.headers().firstValue(AUTHORIZATION_HEADER).get());
        assertEquals(APPLICATION_JSON, request.headers().firstValue(ACCEPT_HEADER).get());
        assertEquals(APPLICATION_JSON, request.headers().firstValue(CONTENT_TYPE_HEADER).get());
    }

    @Test
    void testCreateHttpRequest_WithoutBody() {
        when(mockAuthClient.getJwtToken()).thenReturn(TEST_JWT_TOKEN);
        String method = GET_METHOD;
        String path = "/saved";
        String body = null;

        HttpRequest request = createHttpRequest(userArticleClient, method, path, body);

        assertNotNull(request);
        assertEquals(GET_METHOD, request.method());
        assertTrue(request.headers().firstValue(AUTHORIZATION_HEADER).isPresent());
        assertTrue(request.headers().firstValue(ACCEPT_HEADER).isPresent());
        assertFalse(request.headers().firstValue(CONTENT_TYPE_HEADER).isPresent());
        assertEquals(BEARER_PREFIX + TEST_JWT_TOKEN, request.headers().firstValue(AUTHORIZATION_HEADER).get());
        assertEquals(APPLICATION_JSON, request.headers().firstValue(ACCEPT_HEADER).get());
    }

    @Test
    void testParseResponse_ValidJson() throws Exception {
        JsonNode result = parseResponse(userArticleClient, TEST_ARTICLE_JSON);

        assertNotNull(result);
        assertEquals(1, result.get("id").asInt());
        assertEquals("Test Article", result.get("title").asText());
    }

    @Test
    void testParseResponse_EmptyResponse() throws Exception {
        JsonNode result = parseResponse(userArticleClient, "");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseResponse_PlainText() throws Exception {
        JsonNode result = parseResponse(userArticleClient, SUCCESS_MESSAGE);

        assertNotNull(result);
        assertEquals(SUCCESS_MESSAGE, result.get("message").asText());
    }

    @Test
    void testParseJsonArray_ValidArray() throws Exception {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        ObjectNode article1 = objectMapper.createObjectNode();
        article1.put("id", 1);
        article1.put("title", "Article 1");
        ObjectNode article2 = objectMapper.createObjectNode();
        article2.put("id", 2);
        article2.put("title", "Article 2");
        arrayNode.add(article1);
        arrayNode.add(article2);

        List<JsonNode> result = parseJsonArray(userArticleClient, arrayNode);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).get("id").asInt());
        assertEquals("Article 1", result.get(0).get("title").asText());
        assertEquals(2, result.get(1).get("id").asInt());
        assertEquals("Article 2", result.get(1).get("title").asText());
    }

    @Test
    void testParseJsonArray_EmptyArray() throws Exception {
        ArrayNode arrayNode = objectMapper.createArrayNode();

        List<JsonNode> result = parseJsonArray(userArticleClient, arrayNode);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseJsonArray_NonArray() throws Exception {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("message", "Not an array");

        List<JsonNode> result = parseJsonArray(userArticleClient, objectNode);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testTrimLeadingWhitespace() {
        String text = "   Hello World   ";

        String result = trimLeadingWhitespace(userArticleClient, text);

        assertEquals("Hello World   ", result);
    }

    @Test
    void testTrimLeadingWhitespace_NoLeadingSpaces() {
        String text = "Hello World";

        String result = trimLeadingWhitespace(userArticleClient, text);

        assertEquals("Hello World", result);
    }
} 