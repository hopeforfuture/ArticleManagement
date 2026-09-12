package com.src.articleservice.service;

import com.src.articleservice.client.CategoryClient;
import com.src.articleservice.client.CommentClient;
import com.src.articleservice.dao.ArticleRepository;
import com.src.articleservice.dao.TagRepository;
import com.src.articleservice.dto.CategoryResponse;
import com.src.articleservice.dto.CommentResponse;
import com.src.articleservice.exception.ArticleNotFoundException;
import com.src.articleservice.model.Article;
import com.src.articleservice.model.Tag;
import com.src.articleservice.validation.ArticleRequest;
import com.src.articleservice.dto.ArticleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryClient categoryClient;
    private final TagRepository tagRepository;
    private final SlugService slugService;
    private final CommentClient commentClient;

    Set<String> allowedExtensions = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    public ArticleService(
            ArticleRepository articleRepository,
            CategoryClient categoryClient,
            TagRepository tagRepository,
            SlugService slugService,
            CommentClient commentClient) {

        this.articleRepository = articleRepository;
        this.categoryClient = categoryClient;
        this.tagRepository = tagRepository;
        this.slugService = slugService;
        this.commentClient = commentClient;
    }

    private Set<Tag> getOrCreateTags(Set<String> tagNames) {

        Set<Tag> tags = new HashSet<>();

        if (tagNames == null || tagNames.isEmpty()) {
            return tags;
        }

        for (String tagName : tagNames) {

            String name = tagName.trim();

            if (name.isEmpty()) {
                continue;
            }

            Tag tag =
                    tagRepository.findByNameIgnoreCase(name)
                            .orElseGet(() -> {

                                Tag newTag = new Tag();
                                newTag.setName(name);

                                return tagRepository.save(newTag);
                            });

            tags.add(tag);
        }

        return tags;
    }

    // =====================================================
    // CREATE
    // =====================================================

    @Transactional
    public ArticleResponse createArticle(
            ArticleRequest request,
            Long userId,
            MultipartFile image)
            throws IOException {

        Article article = new Article();

        // 1. Verify category exists
        CategoryResponse category =
                categoryClient.getCategory(
                        request.getCategoryId()
                );

        if (category.getStatus() == null ||
                category.getStatus() != 1) {

            throw new RuntimeException(
                    "Selected category is inactive"
            );
        }

        article.setTitle(request.getTitle());
        article.setBody(request.getBody());
        article.setCategoryId(request.getCategoryId());
        String slug = slugService.generateUniqueSlug(
                request.getTitle(),
                articleRepository
        );

        article.setSlug(slug);

        // Get user ID from JWT
        article.setUserId(userId);

        // New articles are unpublished
        article.setIsPublished(false);

        if (image != null && !image.isEmpty()) {

            String contentType = image.getContentType();

            System.out.println("Image name: "
                    + image.getOriginalFilename());

            System.out.println("Image content type: "
                    + contentType);

            String fileName =
                    image.getOriginalFilename();

            String extension =
                    fileName.substring(
                            fileName.lastIndexOf(".") + 1
                    ).toLowerCase();


            if (!allowedExtensions.contains(extension)) {
                throw new RuntimeException(
                        "Only JPEG, PNG and WEBP images are allowed"
                );
            }

            if (image.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException(
                        "Image size must not exceed 5 MB"
                );
            }

            article.setImageName(fileName);
            article.setImageType(contentType);
            article.setImage(image.getBytes());
        }

        Set<Tag> tags =
                getOrCreateTags(request.getTags());

        article.setTags(tags);

        Article saved =
                articleRepository.save(article);

        return convertToResponse(saved);
    }


    // =====================================================
    // GET SINGLE ARTICLE
    // =====================================================

    @Transactional(readOnly = true)
    public ArticleResponse getArticle(Long id) {

        Article article =
                articleRepository.findById(id)
                        .orElseThrow(() ->
                                new ArticleNotFoundException(
                                        "Article not found with id: "
                                                + id
                                )
                        );

        return convertToResponse(article);
    }


    // =====================================================
    // GET ALL ARTICLES
    // =====================================================

    @Transactional(readOnly = true)
    public List<ArticleResponse> getAllArticles() {

        return articleRepository
                .findAllByOrderByIdDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =====================================================
    // GET MY ARTICLES
    // =====================================================

    @Transactional(readOnly = true)
    public List<ArticleResponse> getMyArticles(
            Long userId) {

        return articleRepository
                .findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =====================================================
    // UPDATE ARTICLE
    // =====================================================

    @Transactional
    public ArticleResponse updateArticle(
            Long articleId,
            ArticleRequest request,
            Long userId,
            MultipartFile image)
            throws IOException {

        Article article =
                articleRepository.findById(articleId)
                        .orElseThrow(() ->
                                new ArticleNotFoundException(
                                        "Article not found with id: "
                                                + articleId
                                )
                        );

        // OWNERSHIP CHECK
        if (!article.getUserId().equals(userId)) {

            throw new RuntimeException(
                    "You are not allowed to edit this article"
            );
        }

        // 1. Verify category exists
        CategoryResponse category =
                categoryClient.getCategory(
                        request.getCategoryId()
                );

        if (category.getStatus() == null ||
                category.getStatus() != 1) {

            throw new RuntimeException(
                    "Selected category is inactive"
            );
        }

        article.setTitle(request.getTitle());
        article.setBody(request.getBody());
        article.setCategoryId(request.getCategoryId());

        System.out.println("Article Slug: " + article.getSlug());

        if ((!article.getTitle().equals(request.getTitle())) || (article.getSlug().trim().isEmpty())) {

            String newSlug = slugService.generateUniqueSlugForUpdate(
                    request.getTitle(),
                    article.getId(),
                    articleRepository
            );

            article.setSlug(newSlug);
        }

        if (image != null && !image.isEmpty()) {

            String contentType = image.getContentType();
            String fileName =
                    image.getOriginalFilename();

            String extension =
                    fileName.substring(
                            fileName.lastIndexOf(".") + 1
                    ).toLowerCase();


            if (!allowedExtensions.contains(extension)) {
                throw new RuntimeException(
                        "Only JPEG, PNG and WEBP images are allowed"
                );
            }

            if (image.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException(
                        "Image size must not exceed 5 MB"
                );
            }

            article.setImageName(fileName);
            article.setImageType(contentType);
            article.setImage(image.getBytes());
        }
        Set<Tag> tags =
                getOrCreateTags(request.getTags());

        article.getTags().clear();
        article.getTags().addAll(tags);
        Article updated =
                articleRepository.save(article);

        return convertToResponse(updated);
    }


    // =====================================================
    // DELETE ARTICLE
    // =====================================================

    @Transactional
    public void deleteArticle(
            Long articleId,
            Long userId) {

        Article article =
                articleRepository.findById(articleId)
                        .orElseThrow(() ->
                                new ArticleNotFoundException(
                                        "Article not found with id: "
                                                + articleId
                                )
                        );

        // OWNERSHIP CHECK
        if (!article.getUserId().equals(userId)) {

            throw new RuntimeException(
                    "You are not allowed to delete this article"
            );
        }

        articleRepository.delete(article);
    }


    // =====================================================
    // PUBLISH
    // =====================================================

    @Transactional
    public ArticleResponse publishArticle(
            Long articleId,
            Long userId) {

        Article article =
                articleRepository.findById(articleId)
                        .orElseThrow(() ->
                                new ArticleNotFoundException(
                                        "Article not found with id: "
                                                + articleId
                                )
                        );

        // Only owner can publish
        /*if (!article.getUserId().equals(userId)) {

            throw new RuntimeException(
                    "You are not allowed to publish this article"
            );
        }*/

        article.setIsPublished(true);

        return convertToResponse(
                articleRepository.save(article)
        );
    }


    // =====================================================
    // ADMIN UNPUBLISH
    // =====================================================

    @Transactional
    public ArticleResponse unpublishArticle(
            Long articleId) {

        Article article =
                articleRepository.findById(articleId)
                        .orElseThrow(() ->
                                new ArticleNotFoundException(
                                        "Article not found with id: "
                                                + articleId
                                )
                        );

        article.setIsPublished(false);

        return convertToResponse(
                articleRepository.save(article)
        );
    }


    // =====================================================
    // IMAGE
    // =====================================================

    @Transactional(readOnly = true)
    public Article getArticleImage(Long articleId) {

        return articleRepository.findById(articleId)
                .orElseThrow(() ->
                        new ArticleNotFoundException(
                                "Article not found with id: "
                                        + articleId
                        )
                );
    }


    // =====================================================
    // RESPONSE CONVERTER
    // =====================================================

    private ArticleResponse convertToResponse(
            Article article) {

        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getBody(),
                article.getImageName(),
                article.getImageType(),
                article.getCategoryId(),
                article.getUserId(),
                article.getIsPublished(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }

    public List<CommentResponse> getCommentsByArticle(
            Long articleId) {

        // First verify that article exists
        articleRepository.findById(articleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Article not found: " + articleId
                        )
                );

        return commentClient.getComments(articleId);
    }
}