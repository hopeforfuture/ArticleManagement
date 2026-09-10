package com.src.articleservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "articles",
        indexes = {
                @Index(name = "idx_article_user", columnList = "user_id"),
                @Index(name = "idx_article_category", columnList = "category_id"),
                @Index(name = "idx_article_published", columnList = "is_published")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    private String imageName;

    private String imageType;

    @Lob
    private byte[] image;

    /**
     * ID of category from Category Service.
     *
     * We intentionally store only the ID.
     * No @ManyToOne because Category belongs
     * to another microservice.
     */
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    /**
     * ID of user from User Service.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @ManyToMany
    @JoinTable(
            name = "articles_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}