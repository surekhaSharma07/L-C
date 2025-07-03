package com.intimetec.newsaggreation.model;


import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reactions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "news_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private Article news;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;
}
