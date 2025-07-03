// src/main/java/com/intimetec/newsaggregation/client/UserArticleClient.java
package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class UserArticleClient {

    private static final String BASE = "http://localhost:8081/api/user/articles";

    private final AuthClient auth;
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newHttpClient();

    /* ------------------------------------------------- public -------- */
    public void save(long id) {
        post("/save?articleId=" + id, null);
    }

    public void unsave(long articleId) {
        delete(articleId);
    }

    public void like(long id) {
        react(id, true);
    }

    public void dislike(long id) {
        react(id, false);
    }


    public List<JsonNode> listSaved() {
        JsonNode root = send("GET", "/saved", null);
        if (root.isArray()) {
            List<JsonNode> result = new ArrayList<>();
            root.forEach(result::add);
            return result;
        } else {
            return Collections.emptyList();
        }
    }
    // shortcut – already an array


    /* ------------------------------------------------- internal ------ */
    private void react(long id, boolean like) {
        put("/reaction?articleId=" + id + "&like=" + like, null);
    }

    /* ------------------ tiny wrappers ------------------------------- */
    private JsonNode get(String p) {
        return send("GET", p, null);
    }

    private JsonNode post(String p, String b) {
        return send("POST", p, b);
    }

    private JsonNode put(String p, String b) {
        return send("PUT", p, b);
    }


    private void delete(long articleId) {
        // 2️⃣  build the URL that your controller expects
        send("DELETE", "/save/" + articleId, null);
    }

    /* ------------------ the only place that changed ----------------- */
    private JsonNode send(String method, String path, String body) {
        try {
            HttpRequest.Builder b = HttpRequest.newBuilder(
                            URI.create(BASE + path))
                    .header("Authorization", "Bearer " + auth.getJwtToken())
                    .header("Accept", "application/json");

            if (body != null)
                b.header("Content-Type", "application/json")
                        .method(method, HttpRequest.BodyPublishers.ofString(body));
            else
                b.method(method, HttpRequest.BodyPublishers.noBody());

            HttpResponse<String> r =
                    http.send(b.build(), HttpResponse.BodyHandlers.ofString());

            if (r.statusCode() / 100 != 2)
                throw new RuntimeException("HTTP " + r.statusCode() + " – " + r.body());

            /* >>> tolerant JSON / plain‑text handling <<< */
            String txt = r.body();
            if (txt == null || txt.isBlank())
                return mapper.createObjectNode();           // empty

            // skip leading whitespace
            int i = 0;
            while (i < txt.length() && Character.isWhitespace(txt.charAt(i))) i++;
            char first = i < txt.length() ? txt.charAt(i) : '\0';

            if (first == '{' || first == '[') {
                return mapper.readTree(txt);                // valid JSON
            } else {
                return mapper.createObjectNode()
                        .put("message", txt);          // wrap plain text
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
