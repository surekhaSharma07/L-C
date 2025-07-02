package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

/**
 * Helper for /api/notify routes
 *
 * ┌─ GET  /api/notify/prefs   → {@link #fetchConfig()}
 * ├─ PUT  /api/notify/prefs   → {@link #saveConfig(JsonNode)}
 * └─ GET  /api/notify         → {@link #fetchNotifications()}
 *
 * It re‑uses the JWT that {@link AuthClient} already stores.
 */
public class NotificationClient {

    private static final String PREFS = "/api/notify/prefs";
    private static final String NOTE  = "/api/notify";

    private final ObjectMapper mapper;
    private final AuthClient   auth;        // gives us the Bearer token
    private final HttpClient   http = HttpClient.newHttpClient();
    private final String       base = "http://localhost:8081";  // same host used everywhere

    public NotificationClient(ObjectMapper mapper, AuthClient auth) {
        this.mapper = mapper;
        this.auth   = auth;
    }

    /* ---------------- public --------------- */

    /** Current notification configuration for the logged‑in user. */
    public JsonNode fetchConfig() throws Exception {
        HttpRequest req = reqBuilder(PREFS).GET().build();
        return sendJson(req, "fetch prefs");
    }

    /**
     * Saves / updates the configuration you pass in (ObjectNode from ConsoleMenu),
     * returns the **updated** config object from the server.
     */
    public JsonNode saveConfig(JsonNode body) throws Exception {
        HttpRequest req = reqBuilder(PREFS)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        return sendJson(req, "save prefs");
    }

    /** List notifications already generated for the current user. */
    public List<JsonNode> fetchNotifications() throws Exception {
        HttpRequest req = reqBuilder(NOTE).GET().build();
        JsonNode arr = sendJson(req, "fetch notifications");
        return Arrays.asList(mapper.convertValue(arr, JsonNode[].class));
    }

    /* ------------ used by ConsoleMenu -------------- */
    public ObjectMapper getMapper() {        // ConsoleMenu needs this in manageKeywords()
        return mapper;
    }

    /* --------------- helpers -------------- */
    private HttpRequest.Builder reqBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(base + path))
                .header("Authorization", "Bearer " + auth.getJwtToken());
    }

    private JsonNode sendJson(HttpRequest req, String action) throws Exception {
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("Failed to " + action + " – HTTP " +
                    res.statusCode() + " → " + res.body());
        }
        return mapper.readTree(res.body());
    }


    /* ---------- static helpers (optional) ---------- */

    /**
     * Create a keyword array for the expected payload format:
     * [
     *   { "id": null, "term": "bitcoin" },
     *   { "id": null, "term": "election" }
     * ]
     */
    public static ArrayNode toKeywordArray(ObjectMapper m, List<String> words) {
        ArrayNode arr = m.createArrayNode();
        for (String w : words) {
            ObjectNode obj = m.createObjectNode();
            obj.putNull("id");
            obj.put("term", w);
            arr.add(obj);
        }
        return arr;
    }
}
