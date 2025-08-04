package com.intimetec.newsaggreation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.intimetec.newsaggreation.model.Article;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {
    private Long id;
    private String title;
    private String description;
    private String url;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String publishedAt;
    private String primaryCategory;
    private List<String> categories;

    public static ArticleResponse from(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getDescription(),
                article.getUrl(),
                article.getPublishedAt().toString(),
                article.getPrimaryCategory() != null
                        ? article.getPrimaryCategory().getName()
                        : null,
                article.getCategories().stream()
                        .map(category -> category.getName())
                        .toList()
        );
    }
}
