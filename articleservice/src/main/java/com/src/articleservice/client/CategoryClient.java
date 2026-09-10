package com.src.articleservice.client;

import com.src.articleservice.dto.CategoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "category-service",
        url = "${category.service.url}"
)
public interface CategoryClient {

    @GetMapping("/api/v1/categories/{id}")
    CategoryResponse getCategory(@PathVariable Long id);
}