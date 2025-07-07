package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.exception.NetworkException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

public class NewsClient {

    private static final String BASE_URL = "http://localhost:8081/api/news";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final AuthClient authClient;

    public NewsClient(ObjectMapper mapper, AuthClient authClient) {
        this.mapper = mapper;
        this.authClient = authClient;
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<JsonNode> fetchToday() throws Exception {
        return getArray(BASE_URL + "/today");
    }

    public List<JsonNode> fetchByDateRange(LocalDate from, LocalDate to) throws Exception {
        String url = buildDateRangeUrl(from, to);
        return getArray(url);
    }

    public List<JsonNode> fetchByDateAndCategory(LocalDate date, String category) throws Exception {
        String url = buildDateCategoryUrl(date, category);
        return getArray(url);
    }

    public List<JsonNode> search(String query) throws Exception {
        return search(query, null, null);
    }


    public List<JsonNode> search(String query, LocalDate from, LocalDate to) throws Exception {
        String url = buildSearchUrl(query, from, to);
        return getArray(url);
    }


    private String buildDateRangeUrl(LocalDate from, LocalDate to) {
        return BASE_URL + "?from=" + from + "&to=" + to;
    }

    private String buildDateCategoryUrl(LocalDate date, String category) {
        return BASE_URL + "?date=" + date + "&category=" + category;
    }


    private String buildSearchUrl(String query, LocalDate from, LocalDate to) {
        StringBuilder url = new StringBuilder(BASE_URL)
                .append("/search?query=")
                .append(URLEncoder.encode(query, StandardCharsets.UTF_8));

        if (from != null && to != null) {
            url.append("&from=").append(from).append("&to=").append(to);
        }

        return url.toString();
    }

    private List<JsonNode> getArray(String url) throws Exception {
        try {
            HttpRequest request = createGetRequest(url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            validateResponse(response);
            return parseJsonArray(response.body());
        } catch (NetworkException exception) {
            throw new Exception(exception.getMessage(), exception);
        } catch (Exception exception) {
            throw exception;
        }
    }

    private HttpRequest createGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authClient.getJwtToken())
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    private void validateResponse(HttpResponse<String> response) {
        if (response.statusCode() / 100 != 2) {
            throw new NetworkException("HTTP " + response.statusCode() + " → " + response.body(), response.statusCode());
        }
    }

    private List<JsonNode> parseJsonArray(String responseBody) throws Exception {
        JsonNode tree = mapper.readTree(responseBody);
        return mapper.convertValue(tree, new TypeReference<>() {
        });
    }
}
