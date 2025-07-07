package com.intimetec.newsaggregation.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class AdminClient {

    private final String baseUrl;
    private final String jwt;
    private final RestTemplate rest = new RestTemplate();

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
        } catch (Exception e) {
            if (e.getMessage().contains("LocalDateTime")) {
                System.out.println("Server serialization issue detected. Reports feature temporarily unavailable.");
                return new ReportDto[0];
            }
            throw e;
        }
    }


    private void postNoBody(String path) {
        exchange(path, HttpMethod.POST, null, Void.class);
    }

    private <T> T exchange(String path, HttpMethod method, Object body, Class<T> type) {
        HttpHeaders hdr = new HttpHeaders();
        hdr.setBearerAuth(jwt);
        hdr.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = body == null ? new HttpEntity<>(hdr) : new HttpEntity<>(body, hdr);
        ResponseEntity<T> resp = rest.exchange(baseUrl + path, method, entity, type);
        return resp.getBody();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiSourceDto {
        private Integer id;
        private String name;
        private String endpointUrl;
        private String apiKey;
        private Integer pollingFreq;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportDto {
        private String userEmail;
        private String reportedAt;
        private String reason;
    }
}
