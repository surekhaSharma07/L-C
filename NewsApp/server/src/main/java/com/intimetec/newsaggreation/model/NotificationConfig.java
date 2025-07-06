package com.intimetec.newsaggreation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_configs")
public class NotificationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* one‑to‑one per user; keep ManyToOne if you already rely on it */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /* existing fields */
    private String channel;
    private LocalDateTime time;
    private boolean enabled;

    /* NEW — category toggles */
    private boolean business;
    private boolean entertainment;
    private boolean sports;
    private boolean technology;

    /* NEW — keywords linked to this config */
    @ManyToMany
    @JoinTable(name = "config_keywords",
            joinColumns = @JoinColumn(name = "config_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private Set<Keyword> keywords = new HashSet<>();
}
