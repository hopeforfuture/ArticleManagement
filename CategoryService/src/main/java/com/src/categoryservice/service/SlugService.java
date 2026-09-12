package com.src.categoryservice.service;

import com.github.slugify.Slugify;
import com.src.categoryservice.dao.CategoryRepository;
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
            CategoryRepository categoryRepository) {

        String baseSlug = generateSlug(title);
        String slug = baseSlug;

        int counter = 1;

        while (categoryRepository.existsBySlug(slug)) {

            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    public String generateUniqueSlugForUpdate(
            String title,
            Long CategoryId,
            CategoryRepository categoryRepository) {

        String baseSlug = generateSlug(title);
        String slug = baseSlug;

        int counter = 1;

        while (categoryRepository.existsBySlugAndIdNot(slug, CategoryId)) {

            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}