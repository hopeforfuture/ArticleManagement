package com.src.articleservice.client;

import com.src.articleservice.dto.CommentResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "comment-service",
        url = "${comment.service.url}"
)
public interface CommentClient {

    @GetMapping("/api/v1/comments/article/{articleId}")
    List<CommentResponse> getComments(
            @PathVariable Long articleId
    );
}