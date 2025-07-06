////package com.intimetec.newsaggreation.scheduler;
////
////import com.fasterxml.jackson.databind.JsonNode;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.intimetec.newsaggreation.model.ApiSource;
////import com.intimetec.newsaggreation.model.Article;
////import com.intimetec.newsaggreation.model.Category;
////import com.intimetec.newsaggreation.repository.ApiSourceRepository;
////import com.intimetec.newsaggreation.service.ArticleService;
////import com.intimetec.newsaggreation.service.CategoryService;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////import org.springframework.http.HttpEntity;
////import org.springframework.http.HttpHeaders;
////import org.springframework.http.HttpMethod;
////import org.springframework.http.ResponseEntity;
////import org.springframework.scheduling.annotation.Scheduled;
////import org.springframework.stereotype.Component;
////import org.springframework.web.client.RestTemplate;
////
////import java.net.URI;
////import java.net.URLDecoder;
////import java.nio.charset.StandardCharsets;
////import java.time.LocalDateTime;
////import java.time.format.DateTimeFormatter;
////import java.util.Arrays;
////import java.util.HashSet;
////import java.util.List;
////import java.util.Set;
////
////@Component
////public class NewsFetchScheduler {
////    private static final Logger logger = LoggerFactory.getLogger(NewsFetchScheduler.class);
////
////    private final ApiSourceRepository apiSourceRepository;
////    private final ArticleService articleService;
////    private final CategoryService categoryService;
////    private final RestTemplate restTemplate;
////    private final ObjectMapper objectMapper;
////
////    public NewsFetchScheduler(ApiSourceRepository apiSourceRepository,
////                              ArticleService articleService,
////                              CategoryService categoryService,
////                              RestTemplate restTemplate,
////                              ObjectMapper objectMapper) {
////        this.apiSourceRepository = apiSourceRepository;
////        this.articleService = articleService;
////        this.categoryService = categoryService;
////        this.restTemplate = restTemplate;
////        this.objectMapper = objectMapper;
////    }
////
////    @Scheduled(fixedRateString = "${app.fetch-rate-ms}")
////    public void runScheduledFetch() {
////        List<ApiSource> sources = apiSourceRepository.findAll();
////        for (ApiSource source : sources) {
////            String endpoint = source.getEndpointUrl();
////            HttpHeaders requestHeaders = null;
////
////            switch (source.getName()) {
////                case "NewsAPI":
////                    endpoint += (endpoint.contains("?") ? "&" : "?") + "apiKey=" + source.getApiKey();
////                    break;
////                case "TheNewsAPI":
////                    endpoint += (endpoint.contains("?") ? "&" : "?") + "api_token=" + source.getApiKey();
////                    break;
////                case "FirebaseTest":
////                    requestHeaders = new HttpHeaders();
////                    requestHeaders.add("api-key", source.getApiKey());
////                    break;
////                default:
////                    continue;
////            }
////
////            try {
////                ResponseEntity<String> response = (requestHeaders != null)
////                        ? restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(requestHeaders), String.class)
////                        : restTemplate.getForEntity(endpoint, String.class);
////
////                JsonNode rootNode = objectMapper.readTree(response.getBody());
////                JsonNode items = rootNode.has("data") ? rootNode.get("data") : rootNode.get("articles");
////                if (items != null && items.isArray()) {
////                    for (JsonNode articleNode : items) {
////                        String url = articleNode.path("url").asText(null);
////                        if (url == null || articleService.existsByUrl(url)) {
////                            continue;
////                        }
////
////                        Article articleEntity = new Article();
////                        articleEntity.setTitle(articleNode.path("title").asText(null));
////                        articleEntity.setDescription(articleNode.path("description").asText(null));
////                        articleEntity.setUrl(url);
////
////                        String publishedAtString = articleNode.path("publishedAt").asText(null);
////                        LocalDateTime publishedAt = (publishedAtString != null)
////                                ? LocalDateTime.parse(publishedAtString.replace("Z", ""), DateTimeFormatter.ISO_DATE_TIME)
////                                : LocalDateTime.now();
////                        articleEntity.setPublishedAt(publishedAt);
////
////                        Set<Category> categories = new HashSet<>();
////                        JsonNode categoriesNode = articleNode.get("categories");
////                        if (categoriesNode != null && categoriesNode.isArray()) {
////                            for (JsonNode categoryNode : categoriesNode) {
////                                String name = categoryNode.asText().trim();
////                                if (!name.isEmpty()) {
////                                    categories.add(categoryService.findOrCreate(name));
////                                }
////                            }
////                        }
////
////                        // If no categories and source=NewsAPI, try query parameter
////                        if (categories.isEmpty() && "NewsAPI".equals(source.getName())) {
////                            String categoryParam = extractQueryParameter(source.getEndpointUrl(), "category");
////                            if (categoryParam != null && !categoryParam.isBlank()) {
////                                categories.add(categoryService.findOrCreate(categoryParam));
////                            }
////                        } else if (categories.isEmpty() && articleNode.has("source") && articleNode.get("source").has("name")) {
////                            String fallback = articleNode.get("source").get("name").asText().trim();
////                            categories.add(categoryService.findOrCreate(fallback));
////                        }
////
////                        categories.stream().findFirst().ifPresent(articleEntity::setPrimaryCategory);
////                        articleEntity.setCategories(categories);
////
////                        articleService.save(articleEntity);
////                    }
////                }
////            } catch (Exception exception) {
////                logger.error("Failed to fetch news for source=" + source.getName(), exception);
////            }
////        }
////    }
////
////    /**
////     * Extracts the value of a single query parameter from a URL.
////     */
////    private String extractQueryParameter(String url, String parameterKey) {
////        try {
////            String rawQuery = new URI(url).getRawQuery();
////            if (rawQuery == null) return null;
////            return Arrays.stream(rawQuery.split("&"))
////                    .map(pair -> pair.split("=", 2))
////                    .filter(kv -> kv[0].equals(parameterKey) && kv.length == 2)
////                    .map(kv -> URLDecoder.decode(kv[1], StandardCharsets.UTF_8))
////                    .findFirst()
////                    .orElse(null);
////        } catch (Exception e) {
////            return null;
////        }
////    }
////}
//package com.intimetec.newsaggreation.scheduler;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.intimetec.newsaggreation.model.ApiSource;
//import com.intimetec.newsaggreation.model.Article;
//import com.intimetec.newsaggreation.model.Category;
//import com.intimetec.newsaggreation.repository.ApiSourceRepository;
//import com.intimetec.newsaggreation.service.ArticleService;
//import com.intimetec.newsaggreation.service.CategoryService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URI;
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
///**
// * Same functionality as the original NewsFetchScheduler, but decomposed into
// * small, self‑describing private methods. No behavioural change.
// */
//@Component
//public class NewsFetchScheduler {
//
//    private static final Logger logger = LoggerFactory.getLogger(NewsFetchScheduler.class);
//
//    private final ApiSourceRepository apiSourceRepository;
//    private final ArticleService articleService;
//    private final CategoryService categoryService;
//    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
//
//    public NewsFetchScheduler(ApiSourceRepository apiSourceRepository,
//                              ArticleService articleService,
//                              CategoryService categoryService,
//                              RestTemplate restTemplate,
//                              ObjectMapper objectMapper) {
//        this.apiSourceRepository = apiSourceRepository;
//        this.articleService = articleService;
//        this.categoryService = categoryService;
//        this.restTemplate = restTemplate;
//        this.objectMapper = objectMapper;
//    }
//
//    @Scheduled(fixedRateString = "${app.fetch-rate-ms}")
//    public void runScheduledFetch() {
//        apiSourceRepository.findAll().forEach(this::fetchFromSource);
//    }
//
//    // ---------------------------------------------------------------------
//    // Decomposed helper methods
//    // ---------------------------------------------------------------------
//
//    /** Main per‑source workflow. */
//    private void fetchFromSource(ApiSource source) {
//        try {
//            Endpoint endpoint = buildEndpoint(source);
//            if (endpoint == null) return; // unsupported source
//
//            JsonNode rootNode = fetchRootNode(endpoint);
//            JsonNode items = rootNode.has("data") ? rootNode.get("data") : rootNode.get("articles");
//            if (items != null && items.isArray()) {
//                processItems(items, source);
//            }
//        } catch (Exception exception) {
//            logger.error("Failed to fetch news for source=" + source.getName(), exception);
//        }
//    }
//
//    /** Build URL + headers according to source name (exact same rules as before). */
//    private Endpoint buildEndpoint(ApiSource source) {
//        String endpointUrl = source.getEndpointUrl();
//        HttpHeaders headers = null;
//
//        switch (source.getName()) {
//            case "NewsAPI" -> endpointUrl += (endpointUrl.contains("?") ? "&" : "?") + "apiKey=" + source.getApiKey();
//            case "TheNewsAPI" -> endpointUrl += (endpointUrl.contains("?") ? "&" : "?") + "api_token=" + source.getApiKey();
//            case "FirebaseTest" -> {
//                headers = new HttpHeaders();
//                headers.add("api-key", source.getApiKey());
//            }
//            default -> {
//                return null; // skip unsupported sources (behaviour unchanged)
//            }
//        }
//        return new Endpoint(endpointUrl, headers == null ? new HttpHeaders() : headers);
//    }
//
//    /** Execute HTTP call and parse root JSON node. */
//    private JsonNode fetchRootNode(Endpoint ep) throws Exception {
//        ResponseEntity<String> response = ep.headers.isEmpty()
//                ? restTemplate.getForEntity(ep.url, String.class)
//                : restTemplate.exchange(ep.url, HttpMethod.GET, new HttpEntity<>(ep.headers), String.class);
//        return objectMapper.readTree(response.getBody());
//    }
//
//    /** Iterate over article JSON nodes and persist them. */
//    private void processItems(JsonNode items, ApiSource source) {
//        items.forEach(node -> {
//            try {
//                String url = node.path("url").asText(null);
//                if (url == null || articleService.existsByUrl(url)) return;
//
//                Article article = buildArticle(node, source, url);
//                articleService.save(article);
//            } catch (Exception ex) {
//                logger.warn("Skipping article due to error", ex);
//            }
//        });
//    }
//
//    /** Build Article entity from JSON node. */
//    private Article buildArticle(JsonNode articleNode, ApiSource source, String url) {
//        Article article = new Article();
//        article.setTitle(articleNode.path("title").asText(null));
//        article.setDescription(articleNode.path("description").asText(null));
//        article.setUrl(url);
//
//        article.setPublishedAt(parsePublishedAt(articleNode.path("publishedAt").asText(null)));
//
//        Set<Category> categories = resolveCategories(articleNode, source);
//        categories.stream().findFirst().ifPresent(article::setPrimaryCategory);
//        article.setCategories(categories);
//
//        return article;
//    }
//
//    /** Resolve categories following the exact same rules as original code. */
//    private Set<Category> resolveCategories(JsonNode articleNode, ApiSource source) {
//        Set<Category> categories = new HashSet<>();
//
//        JsonNode categoriesNode = articleNode.get("categories");
//        if (categoriesNode != null && categoriesNode.isArray()) {
//            categoriesNode.forEach(catNode -> addCategory(categories, catNode.asText()));
//        }
//
//        if (categories.isEmpty() && "NewsAPI".equals(source.getName())) {
//            String categoryParam = extractQueryParameter(source.getEndpointUrl(), "category");
//            addCategory(categories, categoryParam);
//        }
//
//        if (categories.isEmpty() && articleNode.has("source") && articleNode.get("source").has("name")) {
//            String fallback = articleNode.get("source").get("name").asText();
//            addCategory(categories, fallback);
//        }
//        return categories;
//    }
//
//    private void addCategory(Set<Category> set, String raw) {
//        if (raw != null && !raw.trim().isEmpty()) {
//            set.add(categoryService.findOrCreate(raw.trim()));
//        }
//    }
//
//    /** Parse publishedAt string or fallback to now. */
//    private LocalDateTime parsePublishedAt(String publishedAtString) {
//        return (publishedAtString != null)
//                ? LocalDateTime.parse(publishedAtString.replace("Z", ""), DateTimeFormatter.ISO_DATE_TIME)
//                : LocalDateTime.now();
//    }
//
//    /** Extracts the value of a query parameter (original logic unchanged). */
//    private String extractQueryParameter(String url, String parameterKey) {
//        try {
//            String rawQuery = new URI(url).getRawQuery();
//            if (rawQuery == null) return null;
//            return Arrays.stream(rawQuery.split("&"))
//                    .map(pair -> pair.split("=", 2))
//                    .filter(kv -> kv[0].equals(parameterKey) && kv.length == 2)
//                    .map(kv -> URLDecoder.decode(kv[1], StandardCharsets.UTF_8))
//                    .findFirst()
//                    .orElse(null);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    // ---------------------------------------------------------------------
//    // Small immutable helper record
//    // ---------------------------------------------------------------------
//    private record Endpoint(String url, HttpHeaders headers) {
//        boolean isEmpty() { return headers == null || headers.isEmpty(); }
//    }
//}

package com.intimetec.newsaggreation.scheduler;

import com.intimetec.newsaggreation.repository.ApiSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Minimal orchestrator: runs on a fixed schedule and hands each ApiSource row
 * to {@link NewsFetchService}.
 */
@Component
public class NewsFetchScheduler {

    private static final Logger log = LoggerFactory.getLogger(NewsFetchScheduler.class);

    private final ApiSourceRepository apiSourceRepo;
    private final NewsFetchService    newsFetchService;

    public NewsFetchScheduler(ApiSourceRepository apiSourceRepo,
                              NewsFetchService newsFetchService) {
        this.apiSourceRepo   = apiSourceRepo;
        this.newsFetchService = newsFetchService;
    }
    @Scheduled(fixedRateString = "${app.fetch-rate-ms}")
    public void runScheduledFetch() {
        apiSourceRepo.findAll().forEach(newsFetchService::processSource);
    }
}
