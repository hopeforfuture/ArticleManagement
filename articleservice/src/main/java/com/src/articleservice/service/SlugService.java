package com.src.articleservice.service;

import com.github.slugify.Slugify;
import com.src.articleservice.dao.ArticleRepository;
import org.springframework.stereotype.Service;

@Service
public class SlugService {

    private final Slugify slugify = Slugify.builder().build();

    public String generateSlug(String title) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Article title cannot be empty");
        }

        return slugify.slugify(title);
    }

    public String generateUniqueSlug(
            String title,
            ArticleRepository articleRepository) {

        String baseSlug = generateSlug(title);
        String slug = baseSlug;

        int counter = 1;

        while (articleRepository.existsBySlug(slug)) {

            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    public String generateUniqueSlugForUpdate(
            String title,
            Long articleId,
            ArticleRepository articleRepository) {

        String baseSlug = generateSlug(title);
        String slug = baseSlug;

        int counter = 1;

        while (articleRepository.existsBySlugAndIdNot(slug, articleId)) {

            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}