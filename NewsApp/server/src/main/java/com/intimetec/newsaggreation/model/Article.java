//package com.intimetec.newsaggreation.model;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.AllArgsConstructor;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Set;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "news")
//public class Article {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String title;
//
//    @Column(length = 2000)
//    private String description;
//
//    private String url;
//
//    private LocalDateTime publishedAt;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id", nullable = true)
//    private Category category;
//
//    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SavedArticle> savedBy;
//
//    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Like> likedBy;
//
//    @ManyToMany
//    @JoinTable(
//            name = "news_keyword",
//            joinColumns = @JoinColumn(name = "news_id"),
//            inverseJoinColumns = @JoinColumn(name = "keyword_id")
//    )
//    private Set<Keyword> keywords;
//}
// src/main/java/com/intimetec/newsaggreation/model/Article.java

package com.intimetec.newsaggreation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String url;

    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category primaryCategory;

    @ManyToMany
    @JoinTable(
            name = "news_category",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedArticle> savedBy;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likedBy;

    @ManyToMany
    @JoinTable(
            name = "news_keyword",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private Set<Keyword> keywords;
}
