//package com.intimetec.newsaggreation.scheduler;
//
//import com.intimetec.newsaggreation.model.ApiSource;
//import com.intimetec.newsaggreation.model.Article;
//import com.intimetec.newsaggreation.repository.ApiSourceRepository;
//import com.intimetec.newsaggreation.service.ArticleService;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//public class NewsFetchScheduler {
//    private static final Logger log=LoggerFactory.getLogger(NewsFetchScheduler.class);
//    private final ApiSourceRepository apiRepo;
//    private final ArticleService articleService;
//    private final RestTemplate rest=new RestTemplate();
//    private final ObjectMapper mapper=new ObjectMapper();
//    public NewsFetchScheduler(ApiSourceRepository a, ArticleService s){this.apiRepo=a;this.articleService=s;}
//    @Scheduled(fixedRateString="${app.fetch-rate-ms}")
//    public void fetchNews(){
//        List<ApiSource> sources=apiRepo.findAll();
//        for(ApiSource s: sources){
//            String url=s.getEndpointUrl()+"?apiKey="+s.getApiKey();
//            try {
//                String json=rest.getForObject(url,String.class);
//                JsonNode articles=mapper.readTree(json).get("articles");
//                if(articles!=null && articles.isArray()){
//                    for(JsonNode n: articles){
//                        Article a=new Article();
//                        a.setTitle(n.get("title").asText());
//                        a.setDescription(n.get("description").asText());
//                        a.setUrl(n.get("url").asText());
//                        a.setPublishedAt(LocalDateTime.parse(n.get("publishedAt").asText()));
//                        articleService.save(a);
//                    }
//                }
//            } catch(Exception e) { log.error("Fetch failed", e); }
//        }
//    }
//}


package com.intimetec.newsaggreation.scheduler;

import com.intimetec.newsaggreation.model.ApiSource;
import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.repository.ApiSourceRepository;
import com.intimetec.newsaggreation.service.ArticleService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NewsFetchScheduler {
    private static final Logger log = LoggerFactory.getLogger(NewsFetchScheduler.class);

    private final ApiSourceRepository apiRepo;
    private final ArticleService articleService;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public NewsFetchScheduler(ApiSourceRepository apiRepo,
                              ArticleService newsService,
                              RestTemplate restTemplate,
                              ObjectMapper mapper) {
        this.apiRepo = apiRepo;
        this.articleService = newsService;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    @Scheduled(fixedRateString = "${app.fetch-rate-ms}")
    public void fetchNews() {
        List<ApiSource> sources = apiRepo.findAll();
        for (ApiSource source : sources) {
            String url = source.getEndpointUrl();
            HttpHeaders headers = null;

            switch (source.getName()) {
                case "NewsAPI":
                    url += (url.contains("?") ? "&" : "?") + "apiKey=" + source.getApiKey();
                    break;
                case "TheNewsAPI":
                    url += (url.contains("?") ? "&" : "?") + "api_token=" + source.getApiKey();
                    break;
                case "FirebaseTest":
                    headers = new HttpHeaders();
                    headers.add("api-key", source.getApiKey());
                    break;
                default:
                    continue;
            }

            try {
                ResponseEntity<String> response;
                System.out.println("abc " + url);
                if (headers != null) {
                    response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
                } else {
                    response = restTemplate.getForEntity(url, String.class);
                }

                JsonNode articles = mapper.readTree(response.getBody()).get("articles");
                if (articles != null && articles.isArray()) {
                    for (JsonNode n : articles) {
                        String articleUrl = n.path("url").asText(null);
                        if (articleUrl == null || articleService.existsByUrl(articleUrl)) {
                            continue;
                        }

                        Article article = new Article();
                        article.setTitle(n.path("title").asText(null));
                        article.setDescription(n.path("description").asText(null));
                        article.setUrl(articleUrl);

                        String dt = n.path("publishedAt").asText(null);
                        LocalDateTime publishedAt = dt != null
                                ? LocalDateTime.parse(dt, DateTimeFormatter.ISO_DATE_TIME)
                                : LocalDateTime.now();
                        article.setPublishedAt(publishedAt);

                        articleService.save(article);
                    }
                }
            } catch (Exception e) {
                log.error("Fetch failed for " + source.getName(), e);
            }
        }
    }
}

