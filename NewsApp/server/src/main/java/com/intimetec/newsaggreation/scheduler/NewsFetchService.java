package com.intimetec.newsaggreation.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggreation.model.ApiSource;
import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.Category;
import com.intimetec.newsaggreation.service.ArticleService;
import com.intimetec.newsaggreation.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
class NewsFetchService {

    private static final Logger log = LoggerFactory.getLogger(NewsFetchService.class);

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    NewsFetchService(ArticleService articleService,
                     CategoryService categoryService,
                     RestTemplate restTemplate,
                     ObjectMapper objectMapper) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    void processSource(ApiSource src) {
        try {
            Endpoint ep = buildEndpoint(src);
            if (ep == null) return;

            JsonNode root = fetchRootNode(ep);
            JsonNode items = root.has("data") ? root.get("data") : root.get("articles");
            if (items != null && items.isArray()) processItems(items, src);

        } catch (Exception ex) {
            log.error("Failed to fetch news for {}", src.getName(), ex);
        }
    }

    private Endpoint buildEndpoint(ApiSource src) {
        return switch (src.getName()) {
            case "NewsAPI" -> Endpoint.withQueryParam(src.getEndpointUrl(), "apiKey", src.getApiKey());
            case "TheNewsAPI" -> Endpoint.withQueryParam(src.getEndpointUrl(), "api_token", src.getApiKey());
            case "FirebaseTest" -> {
                HttpHeaders h = new HttpHeaders();
                h.add("api-key", src.getApiKey());
                yield new Endpoint(src.getEndpointUrl(), h);
            }
            default -> buildEndpointFromMetadata(src);
        };
    }

    private Endpoint buildEndpointFromMetadata(ApiSource src) {
        return Endpoint.withQueryParam(src.getEndpointUrl(), "apiKey", src.getApiKey());
    }


    private JsonNode fetchRootNode(Endpoint ep) throws Exception {
        ResponseEntity<String> resp = ep.headers.isEmpty()
                ? restTemplate.getForEntity(ep.url, String.class)
                : restTemplate.exchange(ep.url, HttpMethod.GET,
                new HttpEntity<>(ep.headers), String.class);
        return objectMapper.readTree(resp.getBody());
    }

    private void processItems(JsonNode items, ApiSource src) {
        items.forEach(node -> {
            try {
                String link = node.path("url").asText(null);
                if (link == null || articleService.existsByUrl(link)) return;
                articleService.save(buildArticle(node, src, link));
            } catch (Exception e) {
                log.warn("Skipping article – {}", e.getMessage());
            }
        });
    }

    private Article buildArticle(JsonNode n, ApiSource src, String link) {
        Article a = new Article();
        a.setTitle(n.path("title").asText(null));
        a.setDescription(n.path("description").asText(null));
        a.setUrl(link);
        a.setPublishedAt(parseDate(n.path("publishedAt").asText(null)));

        Set<Category> cats = resolveCategories(n, src);
        cats.stream().findFirst().ifPresent(a::setPrimaryCategory);
        a.setCategories(cats);
        return a;
    }

    private Set<Category> resolveCategories(JsonNode n, ApiSource src) {
        Set<Category> out = new HashSet<>();
        JsonNode arr = n.get("categories");
        if (arr != null && arr.isArray()) arr.forEach(j -> addCat(out, j.asText()));
        if (out.isEmpty() && "NewsAPI".equals(src.getName()))
            addCat(out, queryParam(src.getEndpointUrl(), "category"));
        if (out.isEmpty() && n.has("source") && n.get("source").has("name"))
            addCat(out, n.get("source").get("name").asText());
        return out;
    }

    private void addCat(Set<Category> set, String raw) {
        if (raw != null && !raw.isBlank()) set.add(categoryService.findOrCreate(raw.trim()));
    }

    private LocalDateTime parseDate(String iso) {
        return iso == null
                ? LocalDateTime.now()
                : LocalDateTime.parse(iso.replace("Z", ""), DateTimeFormatter.ISO_DATE_TIME);
    }

    private String queryParam(String url, String key) {
        try {
            String q = new URI(url).getRawQuery();
            return Arrays.stream(q.split("&"))
                    .map(p -> p.split("=", 2))
                    .filter(kv -> kv.length == 2 && kv[0].equals(key))
                    .map(kv -> URLDecoder.decode(kv[1], StandardCharsets.UTF_8))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private record Endpoint(String url, HttpHeaders headers) {
        static Endpoint withQueryParam(String url, String key, String value) {
            return new Endpoint(
                    url + (url.contains("?") ? "&" : "?") + key + "=" + value,
                    new HttpHeaders()
            );
        }
    }
}
