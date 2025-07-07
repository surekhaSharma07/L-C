package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.exception.NetworkException;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@RequiredArgsConstructor
public class NotificationClient {

    private static final String PREFERENCES = "http://localhost:8081/api/notify/prefs";
    private static final String LIST = "http://localhost:8081/api/notify";

    private final ObjectMapper mapper;
    private final AuthClient auth;
    private final HttpClient client = HttpClient.newHttpClient();


    public JsonNode fetchConfig() {
        return sendNoBody("GET", PREFERENCES);
    }

    public JsonNode saveConfig(JsonNode body) {
        String json = body.toPrettyString();
        return sendJson("PUT", PREFERENCES, json);
    }

    public List<JsonNode> fetchNotifications() {
        JsonNode tree = sendNoBody("GET", LIST);
        return mapper.convertValue(tree, new TypeReference<>() {
        });
    }

    private JsonNode sendNoBody(String method, String url) {
        return sendJson(method, url, null);
    }

    private JsonNode sendJson(String method, String url, String body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                    .header("Authorization", "Bearer " + auth.getJwtToken())
                    .header("Accept", "application/json");

            if (body != null) {
                builder.header("Content-Type", "application/json")
                        .method(method, HttpRequest.BodyPublishers.ofString(body));
            } else {
                builder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> resp = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                throw new NetworkException("Failed – HTTP " + resp.statusCode() + " → " + resp.body(), resp.statusCode());
            }
            return mapper.readTree(resp.body());
        } catch (Exception exception) {
            throw new NetworkException("Network error while accessing notifications", exception);
        }
    }
}
