package com.intimetec.newsaggreation.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> news;

    public Category() {}

    public Category(Integer id, String name, List<Article> news) {
        this.id = id;
        this.name = name;
        this.news = news;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Article> getNews() { return news; }
    public void setNews(List<Article> news) { this.news = news; }
}

