package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    /* ---------------- prefs ---------------- */

    public JsonNode fetchConfig() {
        return sendNoBody("GET", PREFS);
    }

    public JsonNode saveConfig(JsonNode body) {
        // Jackson 2.15.x is perfectly happy with readTree/writeValueAsString
        String json = body.toPrettyString();
        return sendJson("PUT", PREFS, json);
    }

    /* ---------------- notifications list ---------------- */

    public List<JsonNode> fetchNotifications() {
        JsonNode tree = sendNoBody("GET", LIST);
        // avoid convertValue => readValue and raw generics are safe here
        return mapper.convertValue(tree, new TypeReference<>() {
        });
    }

    /* ---------------------------------------------------------------------- */
    /* internal helpers                                                       */
    /* ---------------------------------------------------------------------- */

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
                throw new RuntimeException("Failed – HTTP " + resp.statusCode() + " → " + resp.body());
            }
            return mapper.readTree(resp.body());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
