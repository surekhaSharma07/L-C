package com.intimetec.newsaggreation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "reports",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "article_id"})
)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Article article;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    /** convenience ctor: always “now” */
    public Report(User user, Article article, String reason) {
        this(user, article, reason, LocalDateTime.now());
    }

    /** full ctor used by ModerationService */
    public Report(User user, Article article, String reason, LocalDateTime reportedAt) {
        this.user       = user;
        this.article    = article;
        this.reason     = reason;
        this.reportedAt = reportedAt;   // ← use the value that was passed in
    }
}
