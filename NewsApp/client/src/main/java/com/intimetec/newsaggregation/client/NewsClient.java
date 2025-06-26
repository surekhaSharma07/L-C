package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewsClient {
    private static final String BASE = "http://localhost:8081/api/news";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper;
    private final AuthClient auth;

    public NewsClient(ObjectMapper mapper, AuthClient auth) {
        this.mapper = mapper;
        this.auth = auth;
    }

    public List<JsonNode> fetchToday() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/today"))
                .header("Authorization", "Bearer " + auth.getJwtToken())
                .GET().build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode arr = mapper.readTree(body);
        return toList(arr);
    }

    public List<JsonNode> fetchByDateRange(LocalDate from, LocalDate to) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "?from=" + from + "&to=" + to))
                .header("Authorization", "Bearer " + auth.getJwtToken())
                .GET().build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode arr = mapper.readTree(body);
        return toList(arr);
    }

    public List<JsonNode> fetchByDateAndCategory(LocalDate date, String category) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "?date=" + date + "&category=" + category))
                .header("Authorization", "Bearer " + auth.getJwtToken())
                .GET().build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode arr = mapper.readTree(body);
        return toList(arr);
    }

    private List<JsonNode> toList(JsonNode arr) {
        List<JsonNode> list = new ArrayList<>();
        if (arr != null && arr.isArray()) {
            arr.forEach(list::add);
        }
        return list;
    }
}
