package com.src.commentservice.client;

import com.src.commentservice.dto.ArticleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "article-service",
        url = "${article.service.url}"
)
public interface ArticleClient {

    @GetMapping("/api/v1/articles/{id}")
    ArticleResponse getArticle(
            @PathVariable Long id
    );
}