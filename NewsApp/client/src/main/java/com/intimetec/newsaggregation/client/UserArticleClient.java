// src/main/java/com/intimetec/newsaggregation/client/UserArticleClient.java
package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles user article operations (save, unsave, like, dislike, report) with the server.
 */
@RequiredArgsConstructor
public class UserArticleClient {

    private static final String BASE_URL = "http://localhost:8081/api/user/articles";
    private final AuthClient authClient;
    private final ObjectMapper mapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /* ====================  PUBLIC OPERATIONS  =========================== */

    /**
     * Saves an article for the current user.
     * 
     * @param articleId the ID of the article to save
     */
    public void save(long articleId) {
        post("/save?articleId=" + articleId, null);
    }

    /**
     * Removes an article from the user's saved articles.
     * 
     * @param articleId the ID of the article to unsave
     */
    public void unsave(long articleId) {
        delete(articleId);
    }

    /**
     * Likes an article.
     * 
     * @param articleId the ID of the article to like
     */
    public void like(long articleId) {
        react(articleId, true);
    }

    /**
     * Dislikes an article.
     * 
     * @param articleId the ID of the article to dislike
     */
    public void dislike(long articleId) {
        react(articleId, false);
    }

    /**
     * Reports an article with a reason.
     * 
     * @param articleId the ID of the article to report
     * @param reason the reason for reporting
     */
    public void report(long articleId, String reason) {
        String encodedReason = URLEncoder.encode(reason, StandardCharsets.UTF_8);
        post("/report?articleId=" + articleId + "&reason=" + encodedReason, null);
    }

    /**
     * Lists all saved articles for the current user.
     * 
     * @return list of saved articles
     */
    public List<JsonNode> listSaved() {
        JsonNode root = send("GET", "/saved", null);
        return parseJsonArray(root);
    }

    /* ====================  PRIVATE OPERATIONS  =========================== */

    /**
     * Reacts to an article (like or dislike).
     * 
     * @param articleId the ID of the article to react to
     * @param isLike true for like, false for dislike
     */
    private void react(long articleId, boolean isLike) {
        put("/reaction?articleId=" + articleId + "&like=" + isLike, null);
    }

    /**
     * Deletes a saved article.
     * 
     * @param articleId the ID of the article to delete
     */
    private void delete(long articleId) {
        send("DELETE", "/save/" + articleId, null);
    }

    /* ====================  HTTP WRAPPERS  =========================== */

    /**
     * Sends a GET request.
     */
    private JsonNode get(String path) {
        return send("GET", path, null);
    }

    /**
     * Sends a POST request.
     */
    private JsonNode post(String path, String body) {
        return send("POST", path, body);
    }

    /**
     * Sends a PUT request.
     */
    private JsonNode put(String path, String body) {
        return send("PUT", path, body);
    }

    /* ====================  CORE HTTP HANDLING  =========================== */

    /**
     * Sends an HTTP request and returns the response as JsonNode.
     * 
     * @param method HTTP method (GET, POST, PUT, DELETE)
     * @param path the API path
     * @param body request body (can be null)
     * @return the response as JsonNode
     */
    private JsonNode send(String method, String path, String body) {
        try {
            HttpRequest request = createHttpRequest(method, path, body);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            validateResponse(response);
            return parseResponse(response.body());
            
        } catch (Exception e) {
            throw new RuntimeException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Creates an HTTP request with proper headers and body.
     */
    private HttpRequest createHttpRequest(String method, String path, String body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .header("Authorization", "Bearer " + authClient.getJwtToken())
                .header("Accept", "application/json");

        if (body != null) {
            builder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return builder.build();
    }

    /**
     * Validates the HTTP response.
     */
    private void validateResponse(HttpResponse<String> response) {
        if (response.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + response.statusCode() + " – " + response.body());
        }
    }

    /**
     * Parses the response body into JsonNode.
     * Handles both JSON and plain text responses.
     */
    private JsonNode parseResponse(String responseBody) throws Exception {
        if (responseBody == null || responseBody.isBlank()) {
            return mapper.createObjectNode(); // empty response
        }

        String trimmedBody = trimLeadingWhitespace(responseBody);
        char firstChar = trimmedBody.isEmpty() ? '\0' : trimmedBody.charAt(0);

        if (firstChar == '{' || firstChar == '[') {
            return mapper.readTree(trimmedBody); // valid JSON
        } else {
            return mapper.createObjectNode().put("message", trimmedBody); // wrap plain text
        }
    }

    /**
     * Trims leading whitespace from a string.
     */
    private String trimLeadingWhitespace(String text) {
        int startIndex = 0;
        while (startIndex < text.length() && Character.isWhitespace(text.charAt(startIndex))) {
            startIndex++;
        }
        return text.substring(startIndex);
    }

    /**
     * Parses a JSON array response into a List of JsonNodes.
     */
    private List<JsonNode> parseJsonArray(JsonNode root) {
        if (root.isArray()) {
            List<JsonNode> result = new ArrayList<>();
            root.forEach(result::add);
            return result;
        } else {
            return Collections.emptyList();
        }
    }
}
