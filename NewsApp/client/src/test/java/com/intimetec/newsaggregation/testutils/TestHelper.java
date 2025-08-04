package com.intimetec.newsaggregation.testutils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.http.HttpRequest;
import java.util.List;

public final class TestHelper {
    
    private TestHelper() {
    }

    public static HttpRequest createHttpRequest(Object client, String method, String path, String body) {
        try {
            java.lang.reflect.Method methodRef = client.getClass().getDeclaredMethod("createHttpRequest", String.class, String.class, String.class);
            methodRef.setAccessible(true);
            return (HttpRequest) methodRef.invoke(client, method, path, body);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to create HTTP request", exception);
        }
    }

    public static JsonNode parseResponse(Object client, String responseBody) throws Exception {
        java.lang.reflect.Method method = client.getClass().getDeclaredMethod("parseResponse", String.class);
        method.setAccessible(true);
        return (JsonNode) method.invoke(client, responseBody);
    }

    public static List<JsonNode> parseJsonArray(Object client, JsonNode root) throws Exception {
        java.lang.reflect.Method method = client.getClass().getDeclaredMethod("parseJsonArray", JsonNode.class);
        method.setAccessible(true);
        return (List<JsonNode>) method.invoke(client, root);
    }

    public static String trimLeadingWhitespace(Object client, String text) {
        try {
            java.lang.reflect.Method method = client.getClass().getDeclaredMethod("trimLeadingWhitespace", String.class);
            method.setAccessible(true);
            return (String) method.invoke(client, text);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to trim whitespace", exception);
        }
    }

    public static String createAuthRequestBody(Object client, String email, String password) throws Exception {
        java.lang.reflect.Method method = client.getClass().getDeclaredMethod("createAuthRequestBody", String.class, String.class);
        method.setAccessible(true);
        return (String) method.invoke(client, email, password);
    }

    public static HttpRequest createPostRequest(Object client, String url, String body) {
        try {
            java.lang.reflect.Method method = client.getClass().getDeclaredMethod("createPostRequest", String.class, String.class);
            method.setAccessible(true);
            return (HttpRequest) method.invoke(client, url, body);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to create POST request", exception);
        }
    }

    public static List<JsonNode> parseNotifications(ObjectMapper mapper, String jsonArray) {
        try {
            return mapper.convertValue(mapper.readTree(jsonArray), 
                new TypeReference<List<JsonNode>>() {});
        } catch (Exception exception) {
            throw new RuntimeException("Failed to parse notifications", exception);
        }
    }

    public static JsonNode parseJson(ObjectMapper mapper, String jsonString) {
        try {
            return mapper.readTree(jsonString);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to parse JSON", exception);
        }
    }
} 