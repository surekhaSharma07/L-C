package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewsClient {

    private static final String BASE = "http://localhost:8081/api/news";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper;
    private final AuthClient auth;

    /* ------------------------------------------------------------ */
    public NewsClient(ObjectMapper mapper, AuthClient auth) {
        this.mapper = mapper;
        this.auth = auth;
    }

    /* ====================  HEADLINES  =========================== */

    public List<JsonNode> fetchToday() throws Exception {
        return getArray(BASE + "/today");
    }

    public List<JsonNode> fetchByDateRange(LocalDate from, LocalDate to) throws Exception {
        return getArray(BASE + "?from=" + from + "&to=" + to);
    }

    public List<JsonNode> fetchByDateAndCategory(LocalDate date, String category) throws Exception {
        return getArray(BASE + "?date=" + date + "&category=" + category);
    }

    /* ====================  SEARCH  ============================== */

    /**
     * Search without date filter.
     */
    public List<JsonNode> search(String q) throws Exception {
        return search(q, null, null);
    }

    /**
     * Search with optional ISO date range.
     */
    public List<JsonNode> search(String q, LocalDate from, LocalDate to) throws Exception {
        StringBuilder url = new StringBuilder(BASE)
                .append("/search?query=")
                .append(URLEncoder.encode(q, StandardCharsets.UTF_8));

        if (from != null && to != null) {
            url.append("&from=").append(from).append("&to=").append(to);
        }

        return getArray(url.toString());
    }



    /* ====================  HELPERS  ============================= */

    /**
     * Execute GET and return JSON‑array as List&lt;JsonNode&gt;.
     */
    private List<JsonNode> getArray(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + auth.getJwtToken())
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> resp =
                http.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + resp.statusCode() + " → " + resp.body());
        }

        // Convert JSON array → List<JsonNode>
        JsonNode tree = mapper.readTree(resp.body());
        return mapper.convertValue(tree, new TypeReference<>() {
        });
    }


}
