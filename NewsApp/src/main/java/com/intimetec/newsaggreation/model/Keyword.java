package com.intimetec.newsaggreation.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "keywords")
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String term;

    @ManyToMany(mappedBy = "keywords")
    private Set<Article> taggedNews = new HashSet<>();

    public Keyword() {
    }

    public Keyword(Long id, String term,
                   Set<Article> taggedNews) {
        this.id = id;
        this.term = term;
        this.taggedNews = taggedNews;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Set<Article> getTaggedNews() {
        return taggedNews;
    }

    public void setTaggedNews(Set<Article> taggedNews) {
        this.taggedNews = taggedNews;
    }
}
