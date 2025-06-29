package com.intimetec.newsaggregation.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@AllArgsConstructor
public class AdminClient {

    private final String baseUrl;
    private final String jwt;
    private final RestTemplate rest = new RestTemplate();

    /* ---------- 1. list external servers ---------- */
    public ApiSourceDto[] listApiSources() {
        return exchange("/api/admin/apisources", HttpMethod.GET, null, ApiSourceDto[].class);
    }

    /* ---------- 2. server detail ---------- */
    public ApiSourceDto getApiSource(Integer id) {
        return exchange("/api/admin/apisources/" + id, HttpMethod.GET, null, ApiSourceDto.class);
    }

    /* ---------- 3. create / update server ---------- */
    public ApiSourceDto saveApiSource(ApiSourceDto dto) {
        HttpMethod method = dto.getId() == null ? HttpMethod.POST : HttpMethod.PUT;
        String path = dto.getId() == null
                ? "/api/admin/apisources"
                : "/api/admin/apisources/" + dto.getId();
        return exchange(path, method, dto, ApiSourceDto.class);
    }

    /* ---------- 4. add category ---------- */
    public Map<?, ?> addCategory(String name) {
        return exchange("/api/admin/categories", HttpMethod.POST,
                Map.of("name", name), Map.class);
    }

    /* ---------- helper ---------- */
    private <T> T exchange(String path, HttpMethod method, Object body, Class<T> type) {
        HttpHeaders hdr = new HttpHeaders();
        hdr.setBearerAuth(jwt);
        hdr.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = body == null ? new HttpEntity<>(hdr) : new HttpEntity<>(body, hdr);
        ResponseEntity<T> resp = rest.exchange(baseUrl + path, method, entity, type);
        return resp.getBody();
    }

    /* ====== DTO used only on client side ====== */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ApiSourceDto {
        private Integer id;
        private String name;
        private String endpointUrl;
        private String apiKey;
        private Integer pollingFreq;
        private String status;   // UP / DOWN
    }
}
