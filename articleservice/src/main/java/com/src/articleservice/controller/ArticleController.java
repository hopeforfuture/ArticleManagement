package com.src.articleservice.controller;

import com.src.articleservice.dto.CommentResponse;
import com.src.articleservice.model.Article;
import com.src.articleservice.service.ArticleService;
import com.src.articleservice.validation.ArticleRequest;
import com.src.articleservice.dto.ArticleResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }


    // =====================================================
    // CREATE
    // =====================================================

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArticleResponse> createArticle(

         @Valid @RequestPart("article")
            ArticleRequest request,

            @RequestPart(
                    value = "image",
                    required = false
            )
            MultipartFile image,

            Authentication authentication)
            throws IOException {

        Long userId =
                (Long) authentication.getCredentials();

        return ResponseEntity.ok(
                articleService.createArticle(
                        request,
                        userId,
                        image
                )
        );
    }


    // =====================================================
    // GET ALL
    // =====================================================

    @GetMapping
    public ResponseEntity<List<ArticleResponse>>
    getAllArticles() {

        return ResponseEntity.ok(
                articleService.getAllArticles()
        );
    }


    // =====================================================
    // GET MY ARTICLES
    // =====================================================

    @GetMapping("/my")
    public ResponseEntity<List<ArticleResponse>>
    getMyArticles(Authentication authentication) {

        Long userId = (Long) authentication.getCredentials();
        return ResponseEntity.ok(
                articleService.getMyArticles(
                        userId
                )
        );
    }


    // =====================================================
    // GET SINGLE
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse>
    getArticle(@PathVariable Long id) {

        return ResponseEntity.ok(
                articleService.getArticle(id)
        );
    }


    // =====================================================
    // UPDATE
    // =====================================================

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long id,
            @Valid @RequestPart("article") ArticleRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication
    ) throws IOException {

        Long userId =
                (Long) authentication.getCredentials();

        return ResponseEntity.ok(
                articleService.updateArticle(
                        id,
                        request,
                        userId,
                        image
                )
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteArticle(

            @PathVariable Long id,

            Authentication authentication) {

        Long userId =
                (Long) authentication.getCredentials();

        articleService.deleteArticle(
                id,
                userId
        );

        return ResponseEntity.ok(
                "Article deleted successfully."
        );
    }


    // =====================================================
    // PUBLISH
    // =====================================================

    @PutMapping("/{id}/publish")
    public ResponseEntity<ArticleResponse>
    publishArticle(

            @PathVariable Long id,

            Authentication authentication) {

        Long userId =
                (Long) authentication.getCredentials();

        return ResponseEntity.ok(
                articleService.publishArticle(
                        id,
                        userId
                )
        );
    }


    // =====================================================
    // ADMIN UNPUBLISH
    // =====================================================

    @PutMapping("/{id}/unpublish")
    public ResponseEntity<ArticleResponse>
    unpublishArticle(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                articleService.unpublishArticle(id)
        );
    }


    // =====================================================
    // ARTICLE IMAGE
    // =====================================================

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(
            @PathVariable Long id) {

        Article article =
                articleService.getArticleImage(id);

        if (article.getImage() == null) {

            return ResponseEntity.notFound()
                    .build();
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                article.getImageName() +
                                "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                article.getImageType()
                        )
                )
                .body(article.getImage());
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>>
    getArticleComments(
            @PathVariable Long id) {

        List<CommentResponse> comments =
                articleService.getCommentsByArticle(id);

        return ResponseEntity.ok(comments);
    }
}