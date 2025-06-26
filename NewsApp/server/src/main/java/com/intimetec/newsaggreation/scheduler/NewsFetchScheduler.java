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
//import org.springframework.http.*;
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
//@Component
//public class NewsFetchScheduler {
//    private static final Logger log = LoggerFactory.getLogger(NewsFetchScheduler.class);
//
//    private final ApiSourceRepository apiRepo;
//    private final ArticleService articleService;
//    private final CategoryService categoryService;
//    private final RestTemplate restTemplate;
//    private final ObjectMapper mapper;
//
//    public NewsFetchScheduler(ApiSourceRepository apiRepo,
//                              ArticleService articleService,
//                              CategoryService categoryService,
//                              RestTemplate restTemplate,
//                              ObjectMapper mapper) {
//        this.apiRepo = apiRepo;
//        this.articleService = articleService;
//        this.categoryService = categoryService;
//        this.restTemplate = restTemplate;
//        this.mapper = mapper;
//    }
//
//    @Scheduled(fixedRateString = "${app.fetch-rate-ms}")
//    public void fetchNews() {
//        for (ApiSource s : apiRepo.findAll()) {
//            String url = s.getEndpointUrl();
//            HttpHeaders headers = null;
//
//            switch (s.getName()) {
//                case "NewsAPI":
//                    url += (url.contains("?") ? "&" : "?") + "apiKey=" + s.getApiKey();
//                    break;
//                case "TheNewsAPI":
//                    url += (url.contains("?") ? "&" : "?") + "api_token=" + s.getApiKey();
//                    break;
//                case "FirebaseTest":
//                    headers = new HttpHeaders();
//                    headers.add("api-key", s.getApiKey());
//                    break;
//                default:
//                    continue;
//            }
//
//            try {
//                ResponseEntity<String> resp = (headers != null)
//                        ? restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class)
//                        : restTemplate.getForEntity(url, String.class);
//
//                JsonNode root = mapper.readTree(resp.getBody());
//                JsonNode articles = root.has("data") ? root.get("data") : root.get("articles");
//                if (articles != null && articles.isArray()) {
//                    for (JsonNode n : articles) {
//                        String articleUrl = n.path("url").asText(null);
//                        if (articleUrl == null || articleService.existsByUrl(articleUrl)) {
//                            continue;
//                        }
//
//                        Article article = new Article();
//                        article.setTitle(n.path("title").asText(null));
//                        article.setDescription(n.path("description").asText(null));
//                        article.setUrl(articleUrl);
//
//                        String dt = n.path("publishedAt").asText(null);
//                        LocalDateTime publishedAt = (dt != null)
//                                ? LocalDateTime.parse(dt.replace("Z",""), DateTimeFormatter.ISO_DATE_TIME)
//                                : LocalDateTime.now();
//                        article.setPublishedAt(publishedAt);
//
//                        // build categories set
//                        Set<Category> cats = new HashSet<>();
//                        JsonNode catNode = n.get("categories");
//                        if (catNode != null && catNode.isArray()) {
//                            for (JsonNode c : catNode) {
//                                String name = c.asText().trim();
//                                if (!name.isEmpty()) {
//                                    cats.add(categoryService.findOrCreate(name));
//                                }
//                            }
//                        }
//
//                        // if none from JSON and this is NewsAPI, parse the URL param
//                        if (cats.isEmpty() && "NewsAPI".equals(s.getName())) {
//                            String param = extractQueryParam(s.getEndpointUrl(), "category");
//                            if (param != null && !param.isBlank()) {
//                                cats.add(categoryService.findOrCreate(param));
//                            }
//                        }
//                        // fallback to source.name
//                        else if (cats.isEmpty() && n.has("source") && n.get("source").has("name")) {
//                            String name = n.get("source").get("name").asText().trim();
//                            cats.add(categoryService.findOrCreate(name));
//                        }
//
//                        // set primaryCategory + full set
//                        cats.stream().findFirst().ifPresent(article::setPrimaryCategory);
//                        article.setCategories(cats);
//
//                        articleService.save(article);
//                    }
//                }
//            } catch (Exception e) {
//                log.error("Fetch failed for source=" + s.getName(), e);
//            }
//        }
//    }
//
//    /**
//     * Extracts the value of a single query parameter from a URL.
//     */
//    private String extractQueryParam(String url, String key) {
//        try {
//            String query = new URI(url).getRawQuery();
//            if (query == null) return null;
//            return Arrays.stream(query.split("&"))
//                    .map(p -> p.split("=", 2))
//                    .filter(kv -> kv[0].equals(key) && kv.length == 2)
//                    .map(kv -> URLDecoder.decode(kv[1], StandardCharsets.UTF_8))
//                    .findFirst()
//                    .orElse(null);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
package com.intimetec.newsaggreation.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggreation.model.ApiSource;
import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.Category;
import com.intimetec.newsaggreation.repository.ApiSourceRepository;
import com.intimetec.newsaggreation.service.ArticleService;
import com.intimetec.newsaggreation.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class NewsFetchScheduler {
    private static final Logger logger = LoggerFactory.getLogger(NewsFetchScheduler.class);

    private final ApiSourceRepository apiSourceRepository;
    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public NewsFetchScheduler(ApiSourceRepository apiSourceRepository,
                              ArticleService articleService,
                              CategoryService categoryService,
                              RestTemplate restTemplate,
                              ObjectMapper objectMapper) {
        this.apiSourceRepository = apiSourceRepository;
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRateString = "${app.fetch-rate-ms}")
    public void runScheduledFetch() {
        List<ApiSource> sources = apiSourceRepository.findAll();
        for (ApiSource source : sources) {
            String endpoint = source.getEndpointUrl();
            HttpHeaders requestHeaders = null;

            switch (source.getName()) {
                case "NewsAPI":
                    endpoint += (endpoint.contains("?") ? "&" : "?") + "apiKey=" + source.getApiKey();
                    break;
                case "TheNewsAPI":
                    endpoint += (endpoint.contains("?") ? "&" : "?") + "api_token=" + source.getApiKey();
                    break;
                case "FirebaseTest":
                    requestHeaders = new HttpHeaders();
                    requestHeaders.add("api-key", source.getApiKey());
                    break;
                default:
                    continue;
            }

            try {
                ResponseEntity<String> response = (requestHeaders != null)
                        ? restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(requestHeaders), String.class)
                        : restTemplate.getForEntity(endpoint, String.class);

                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode items = rootNode.has("data") ? rootNode.get("data") : rootNode.get("articles");
                if (items != null && items.isArray()) {
                    for (JsonNode articleNode : items) {
                        String url = articleNode.path("url").asText(null);
                        if (url == null || articleService.existsByUrl(url)) {
                            continue;
                        }

                        Article articleEntity = new Article();
                        articleEntity.setTitle(articleNode.path("title").asText(null));
                        articleEntity.setDescription(articleNode.path("description").asText(null));
                        articleEntity.setUrl(url);

                        String publishedAtString = articleNode.path("publishedAt").asText(null);
                        LocalDateTime publishedAt = (publishedAtString != null)
                                ? LocalDateTime.parse(publishedAtString.replace("Z", ""), DateTimeFormatter.ISO_DATE_TIME)
                                : LocalDateTime.now();
                        articleEntity.setPublishedAt(publishedAt);

                        Set<Category> categories = new HashSet<>();
                        JsonNode categoriesNode = articleNode.get("categories");
                        if (categoriesNode != null && categoriesNode.isArray()) {
                            for (JsonNode categoryNode : categoriesNode) {
                                String name = categoryNode.asText().trim();
                                if (!name.isEmpty()) {
                                    categories.add(categoryService.findOrCreate(name));
                                }
                            }
                        }

                        // If no categories and source=NewsAPI, try query parameter
                        if (categories.isEmpty() && "NewsAPI".equals(source.getName())) {
                            String categoryParam = extractQueryParameter(source.getEndpointUrl(), "category");
                            if (categoryParam != null && !categoryParam.isBlank()) {
                                categories.add(categoryService.findOrCreate(categoryParam));
                            }
                        } else if (categories.isEmpty() && articleNode.has("source") && articleNode.get("source").has("name")) {
                            String fallback = articleNode.get("source").get("name").asText().trim();
                            categories.add(categoryService.findOrCreate(fallback));
                        }

                        categories.stream().findFirst().ifPresent(articleEntity::setPrimaryCategory);
                        articleEntity.setCategories(categories);

                        articleService.save(articleEntity);
                    }
                }
            } catch (Exception exception) {
                logger.error("Failed to fetch news for source=" + source.getName(), exception);
            }
        }
    }

    /**
     * Extracts the value of a single query parameter from a URL.
     */
    private String extractQueryParameter(String url, String parameterKey) {
        try {
            String rawQuery = new URI(url).getRawQuery();
            if (rawQuery == null) return null;
            return Arrays.stream(rawQuery.split("&"))
                    .map(pair -> pair.split("=", 2))
                    .filter(kv -> kv[0].equals(parameterKey) && kv.length == 2)
                    .map(kv -> URLDecoder.decode(kv[1], StandardCharsets.UTF_8))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
