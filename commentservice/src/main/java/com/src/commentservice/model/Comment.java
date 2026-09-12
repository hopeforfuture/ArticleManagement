package com.src.commentservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.*;

@Entity
@Table(
        name = "comments",
        indexes = {

                @Index(
                        name = "idx_comment_article",
                        columnList = "article_id"
                ),

                @Index(
                        name = "idx_comment_user",
                        columnList = "user_id"
                ),

                @Index(
                        name = "idx_comment_parent",
                        columnList = "parent_id"
                ),

                @Index(
                        name = "idx_comment_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "comment_body",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String commentBody;

    @Column(
            name = "article_id",
            nullable = false
    )
    private Long articleId;

    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    /**
     * 1 = Active
     * 0 = Blocked
     */
    @Column(
            nullable = false
    )
    private Integer status = 1;

    /**
     * null = top-level comment
     * value = reply to another comment
     */
    @Column(
            name = "parent_id"
    )
    private Long parentId;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }
}