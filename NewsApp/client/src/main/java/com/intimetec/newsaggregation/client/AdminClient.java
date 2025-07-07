package com.intimetec.newsaggregation.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.intimetec.newsaggregation.dto.ApiSourceDto;
import com.intimetec.newsaggregation.dto.ReportDto;

import java.util.Map;

public class AdminClient {

    private final String baseUrl;
    private final String jwt;
    private final RestTemplate restTemplate = new RestTemplate();

    public AdminClient(String baseUrl, String jwt) {
        this.baseUrl = baseUrl;
        this.jwt = jwt;
    }

    public ApiSourceDto[] listApiSources() {
        return exchange("/api/admin/apisources", HttpMethod.GET, null, ApiSourceDto[].class);
    }

    public ApiSourceDto getApiSource(Integer id) {
        return exchange("/api/admin/apisources/" + id, HttpMethod.GET, null, ApiSourceDto.class);
    }

    public ApiSourceDto saveApiSource(ApiSourceDto dto) {
        HttpMethod method = dto.getId() == null ? HttpMethod.POST : HttpMethod.PUT;
        String path = dto.getId() == null
                ? "/api/admin/apisources"
                : "/api/admin/apisources/" + dto.getId();
        return exchange(path, method, dto, ApiSourceDto.class);
    }

    public Map<?, ?> addCategory(String name) {
        return exchange("/api/categories/admin", HttpMethod.POST,
                Map.of("name", name), Map.class);
    }


    public void hideArticle(long id) {
        postNoBody("/api/admin/articles/visibility/hide/" + id);
    }

    public void unhideArticle(long id) {
        postNoBody("/api/admin/articles/visibility/unhide/" + id);
    }

    public void hideCategory(int catId) {
        postNoBody("/api/admin/articles/visibility/hide/category/" + catId);
    }

    public void unhideCategory(int catId) {
        postNoBody("/api/admin/articles/visibility/unhide/category/" + catId);
    }

    public ReportDto[] fetchReports(long articleId) {
        try {
            return exchange("/api/admin/articles/visibility/reports/" + articleId,
                    HttpMethod.GET, null, ReportDto[].class);
        } catch (Exception exception) {
            if (exception.getMessage().contains("LocalDateTime")) {
                System.out.println("Server serialization issue detected. Reports feature temporarily unavailable.");
                return new ReportDto[0];
            }
            throw exception;
        }
    }

    private void postNoBody(String path) {
        exchange(path, HttpMethod.POST, null, Void.class);
    }

    private <T> T exchange(String path, HttpMethod method, Object body, Class<T> type) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = body == null ? new HttpEntity<>(httpHeaders) : new HttpEntity<>(body, httpHeaders);
        ResponseEntity<T> resp = restTemplate.exchange(baseUrl + path, method, entity, type);
        return resp.getBody();
    }
}
