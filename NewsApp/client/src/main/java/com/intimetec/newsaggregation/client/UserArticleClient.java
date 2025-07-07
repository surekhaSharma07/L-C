package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.exception.NetworkException;
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


@RequiredArgsConstructor
public class UserArticleClient {

    private static final String BASE_URL = "http://localhost:8081/api/user/articles";
    private final AuthClient authClient;
    private final ObjectMapper mapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();


    public void save(long articleId) {
        post("/save?articleId=" + articleId, null);
    }

    public void unsave(long articleId) {
        delete(articleId);
    }

    public void like(long articleId) {
        react(articleId, true);
    }

    public void dislike(long articleId) {
        react(articleId, false);
    }


    public void report(long articleId, String reason) {
        String encodedReason = URLEncoder.encode(reason, StandardCharsets.UTF_8);
        post("/report?articleId=" + articleId + "&reason=" + encodedReason, null);
    }

    public List<JsonNode> listSaved() {
        JsonNode root = send("GET", "/saved", null);
        return parseJsonArray(root);
    }

    private void react(long articleId, boolean isLike) {
        put("/reaction?articleId=" + articleId + "&like=" + isLike, null);
    }

    private void delete(long articleId) {
        send("DELETE", "/save/" + articleId, null);
    }

    private JsonNode get(String path) {
        return send("GET", path, null);
    }

    private JsonNode post(String path, String body) {
        return send("POST", path, body);
    }

    private JsonNode put(String path, String body) {
        return send("PUT", path, body);
    }

    private JsonNode send(String method, String path, String body) throws NetworkException {
        try {
            HttpRequest request = createHttpRequest(method, path, body);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            validateResponse(response);
            return parseResponse(response.body());
            
        } catch (NetworkException networkException) {
            throw networkException;
        } catch (Exception exception) {
            throw new NetworkException("HTTP request failed: " + exception.getMessage(), exception);
        }
    }

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

    private void validateResponse(HttpResponse<String> response) {
        if (response.statusCode() / 100 != 2) {
            throw new NetworkException("HTTP " + response.statusCode() + " – " + response.body(), response.statusCode());
        }
    }

    private JsonNode parseResponse(String responseBody) throws Exception {
        if (responseBody == null || responseBody.isBlank()) {
            return mapper.createObjectNode(); // empty response
        }

        String trimmedBody = trimLeadingWhitespace(responseBody);
        char firstChar = trimmedBody.isEmpty() ? '\0' : trimmedBody.charAt(0);

        if (firstChar == '{' || firstChar == '[') {
            return mapper.readTree(trimmedBody); // valid JSON
        } else {
            return mapper.createObjectNode().put("message", trimmedBody);
        }
    }

    private String trimLeadingWhitespace(String text) {
        int startIndex = 0;
        while (startIndex < text.length() && Character.isWhitespace(text.charAt(startIndex))) {
            startIndex++;
        }
        return text.substring(startIndex);
    }

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
