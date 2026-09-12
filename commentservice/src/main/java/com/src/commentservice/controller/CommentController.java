package com.src.commentservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.src.commentservice.service.CommentService;
import com.src.commentservice.validation.CommentRequest;
import com.src.commentservice.dto.CommentResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(
            CommentService commentService) {

        this.commentService =
                commentService;
    }

    // =====================================================
    // CREATE COMMENT
    // =====================================================

    @PostMapping
    public ResponseEntity<CommentResponse>
    createComment(
            @Valid
            @RequestBody
            CommentRequest request,
            Authentication authentication) {

        Long userId =
                (Long) authentication
                        .getCredentials();

        CommentResponse response =
                commentService.createComment(
                        request,
                        userId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // UPDATE COMMENT
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse>
    updateComment(
            @PathVariable Long id,

            @Valid
            @RequestBody
            CommentRequest request,

            Authentication authentication) {

        Long userId =
                (Long) authentication
                        .getCredentials();

        CommentResponse response =
                commentService.updateComment(
                        id,
                        request.getCommentBody(),
                        userId
                );

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // DELETE COMMENT - ADMIN
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteComment(
            @PathVariable Long id) {

        commentService.deleteComment(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // =====================================================
    // BLOCK COMMENT - ADMIN
    // =====================================================

    @PutMapping("/{id}/block")
    public ResponseEntity<CommentResponse>
    blockComment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                commentService.blockComment(id)
        );
    }

    // =====================================================
    // UNBLOCK COMMENT - ADMIN
    // =====================================================

    @PutMapping("/{id}/unblock")
    public ResponseEntity<CommentResponse>
    unblockComment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                commentService.unblockComment(id)
        );
    }

    // =====================================================
    // GET ARTICLE COMMENTS
    // PUBLIC
    // =====================================================

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentResponse>>
    getCommentsByArticle(
            @PathVariable Long articleId) {

        return ResponseEntity.ok(
                commentService.getCommentsByArticle(
                        articleId
                )
        );
    }
}