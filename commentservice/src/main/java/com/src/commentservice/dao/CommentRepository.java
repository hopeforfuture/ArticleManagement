package com.src.commentservice.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.src.commentservice.model.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository
        extends JpaRepository<Comment, Long> {

    List<Comment>
    findByArticleIdAndStatusOrderByCreatedAtAsc(
            Long articleId,
            Integer status
    );

    List<Comment>
    findByArticleIdAndParentIdAndStatusOrderByCreatedAtAsc(
            Long articleId,
            Long parentId,
            Integer status
    );

    Optional<Comment>
    findByIdAndStatus(
            Long id,
            Integer status
    );
}