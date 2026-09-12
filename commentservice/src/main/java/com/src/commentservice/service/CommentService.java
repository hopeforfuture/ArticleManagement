package com.src.commentservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.src.commentservice.client.ArticleClient;
import com.src.commentservice.dto.ArticleResponse;
import com.src.commentservice.dao.CommentRepository;
import com.src.commentservice.exception.ArticleNotPublishedException;
import com.src.commentservice.exception.CommentNotFoundException;
import com.src.commentservice.exception.InvalidCommentException;
import com.src.commentservice.model.Comment;
import com.src.commentservice.validation.CommentRequest;
import com.src.commentservice.dto.CommentResponse;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final ArticleClient articleClient;

    public CommentService(
            CommentRepository commentRepository,
            ArticleClient articleClient) {

        this.commentRepository =
                commentRepository;

        this.articleClient =
                articleClient;
    }

    // =====================================================
    // CREATE COMMENT
    // =====================================================

    @Transactional
    public CommentResponse createComment(
            CommentRequest request,
            Long userId) {

        // -----------------------------------------------
        // 1. Verify article
        // -----------------------------------------------

        ArticleResponse article;

        try {

            article =
                    articleClient.getArticle(
                            request.getArticleId()
                    );

        } catch (Exception e) {

            throw new InvalidCommentException(
                    "Unable to verify article: "
                            + request.getArticleId()
            );
        }

        if (article == null) {

            throw new InvalidCommentException(
                    "Article not found"
            );
        }

        // -----------------------------------------------
        // 2. Article must be published
        // -----------------------------------------------

        if (article.getIsPublished() == null ||
                !article.getIsPublished()) {

            throw new ArticleNotPublishedException(
                    "Comments can only be posted on published articles"
            );
        }

        // -----------------------------------------------
        // 3. If reply, verify parent comment
        // -----------------------------------------------

        if (request.getParentId() != null) {

            Comment parent =
                    commentRepository.findById(
                            request.getParentId()
                    ).orElseThrow(() ->
                            new CommentNotFoundException(
                                    "Parent comment not found"
                            )
                    );

            // Parent must belong to same article
            if (!parent.getArticleId()
                    .equals(request.getArticleId())) {

                throw new InvalidCommentException(
                        "Parent comment does not belong to this article"
                );
            }

            // Parent must be active
            if (parent.getStatus() != 1) {

                throw new InvalidCommentException(
                        "Cannot reply to a blocked comment"
                );
            }
        }

        // -----------------------------------------------
        // 4. Create comment
        // -----------------------------------------------

        Comment comment =
                new Comment();

        comment.setCommentBody(
                request.getCommentBody().trim()
        );

        comment.setArticleId(
                request.getArticleId()
        );

        // IMPORTANT:
        // User ID comes from JWT
        comment.setUserId(userId);

        comment.setParentId(
                request.getParentId()
        );

        comment.setStatus(1);

        Comment saved =
                commentRepository.save(comment);

        return convertToResponse(saved);
    }

    // =====================================================
    // UPDATE COMMENT
    // =====================================================

    @Transactional
    public CommentResponse updateComment(
            Long commentId,
            String commentBody,
            Long userId) {

        Comment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new CommentNotFoundException(
                                        "Comment not found: "
                                                + commentId
                                )
                        );

        // -----------------------------------------------
        // Only creator can edit
        // -----------------------------------------------

        if (!comment.getUserId().equals(userId)) {

            throw new InvalidCommentException(
                    "You can only edit your own comment"
            );
        }

        // -----------------------------------------------
        // Blocked comment cannot be edited
        // -----------------------------------------------

        if (comment.getStatus() != 1) {

            throw new InvalidCommentException(
                    "Blocked comment cannot be edited"
            );
        }

        if (commentBody == null ||
                commentBody.trim().isEmpty()) {

            throw new InvalidCommentException(
                    "Comment body is required"
            );
        }

        comment.setCommentBody(
                commentBody.trim()
        );

        Comment updated =
                commentRepository.save(comment);

        return convertToResponse(updated);
    }

    // =====================================================
    // DELETE COMMENT - ADMIN
    // =====================================================

    @Transactional
    public void deleteComment(
            Long commentId) {

        Comment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new CommentNotFoundException(
                                        "Comment not found: "
                                                + commentId
                                )
                        );

        commentRepository.delete(comment);
    }

    // =====================================================
    // BLOCK COMMENT - ADMIN
    // =====================================================

    @Transactional
    public CommentResponse blockComment(
            Long commentId) {

        Comment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new CommentNotFoundException(
                                        "Comment not found: "
                                                + commentId
                                )
                        );

        comment.setStatus(0);

        Comment updated =
                commentRepository.save(comment);

        return convertToResponse(updated);
    }

    // =====================================================
    // UNBLOCK COMMENT - ADMIN
    // =====================================================

    @Transactional
    public CommentResponse unblockComment(
            Long commentId) {

        Comment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new CommentNotFoundException(
                                        "Comment not found: "
                                                + commentId
                                )
                        );

        comment.setStatus(1);

        Comment updated =
                commentRepository.save(comment);

        return convertToResponse(updated);
    }

    // =====================================================
    // GET COMMENTS FOR ARTICLE
    // HIERARCHICAL
    // =====================================================

    @Transactional(readOnly = true)
    public List<CommentResponse>
    getCommentsByArticle(Long articleId) {

        List<Comment> comments =
                commentRepository
                        .findByArticleIdAndStatusOrderByCreatedAtAsc(
                                articleId,
                                1
                        );

        return buildCommentTree(comments);
    }

    // =====================================================
    // BUILD HIERARCHICAL TREE
    // =====================================================

    private List<CommentResponse>
    buildCommentTree(
            List<Comment> comments) {

        Map<Long, CommentResponse> responseMap =
                new HashMap<>();

        List<CommentResponse> rootComments =
                new ArrayList<>();

        // -----------------------------------------------
        // Convert all comments to response objects
        // -----------------------------------------------

        for (Comment comment : comments) {

            CommentResponse response =
                    convertToResponse(comment);

            responseMap.put(
                    comment.getId(),
                    response
            );
        }

        // -----------------------------------------------
        // Build hierarchy
        // -----------------------------------------------

        for (Comment comment : comments) {

            CommentResponse response =
                    responseMap.get(
                            comment.getId()
                    );

            Long parentId =
                    comment.getParentId();

            if (parentId == null) {

                // Top-level comment

                rootComments.add(response);

            } else {

                // Reply

                CommentResponse parent =
                        responseMap.get(parentId);

                if (parent != null) {

                    parent.getReplies()
                            .add(response);
                }
            }
        }

        return rootComments;
    }

    // =====================================================
    // CONVERT ENTITY → RESPONSE
    // =====================================================

    private CommentResponse
    convertToResponse(Comment comment) {

        CommentResponse response =
                new CommentResponse();

        response.setId(
                comment.getId()
        );

        response.setCommentBody(
                comment.getCommentBody()
        );

        response.setArticleId(
                comment.getArticleId()
        );

        response.setUserId(
                comment.getUserId()
        );

        response.setParentId(
                comment.getParentId()
        );

        response.setStatus(
                comment.getStatus()
        );

        response.setCreatedAt(
                comment.getCreatedAt()
        );

        response.setUpdatedAt(
                comment.getUpdatedAt()
        );

        return response;
    }
}