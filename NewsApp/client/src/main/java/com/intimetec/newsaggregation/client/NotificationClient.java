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

    private static final String PREFS = "http://localhost:8081/api/notify/prefs";
    private static final String LIST = "http://localhost:8081/api/notify";

    private final ObjectMapper mapper;
    private final AuthClient auth;
    private final HttpClient client = HttpClient.newHttpClient();


    public JsonNode fetchConfig() {
        return sendNoBody("GET", PREFS);
    }

    public JsonNode saveConfig(JsonNode body) {
        String json = body.toPrettyString();
        return sendJson("PUT", PREFS, json);
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
            HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url))
                    .header("Authorization", "Bearer " + auth.getJwtToken())
                    .header("Accept", "application/json");

            if (body != null) {
                b.header("Content-Type", "application/json")
                        .method(method, HttpRequest.BodyPublishers.ofString(body));
            } else {
                b.method(method, HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                throw new NetworkException("Failed – HTTP " + resp.statusCode() + " → " + resp.body(), resp.statusCode());
            }
            return mapper.readTree(resp.body());
        } catch (NetworkException e) {
            throw e;
        } catch (Exception ex) {
            throw new NetworkException("Network error while accessing notifications", ex);
        }
    }
}
