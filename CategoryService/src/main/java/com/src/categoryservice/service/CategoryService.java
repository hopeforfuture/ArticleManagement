package com.src.categoryservice.service;

import com.src.categoryservice.dao.CategoryRepository;
import com.src.categoryservice.model.Category;
import com.src.categoryservice.validation.CategoryRequest;
import com.src.categoryservice.dto.CategoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    @Transactional
    public CategoryResponse createCategory(
            CategoryRequest request) {

        Category parent = null;

        if (request.getParentId() != null) {

            parent = categoryRepository.findById(
                    request.getParentId()
            ).orElseThrow(() ->
                    new RuntimeException(
                            "Parent category not found with id: "
                                    + request.getParentId()
                    )
            );
        }

        Category category = new Category();

        category.setName(request.getName());
        category.setParent(parent);
        category.setStatus(1);

        Category savedCategory =
                categoryRepository.save(category);

        return convertToResponse(savedCategory);
    }

    // GET SINGLE CATEGORY
    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id
                        )
                );

        return convertToResponse(category);
    }

    // GET ALL CATEGORIES
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository
                .findAllByOrderByIdDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // UPDATE
    @Transactional
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found with id: "
                                                + id
                                )
                        );

        Category parent = null;

        if (request.getParentId() != null) {

            // Prevent category from becoming its own parent
            if (id.equals(request.getParentId())) {

                throw new RuntimeException(
                        "Category cannot be its own parent"
                );
            }

            parent = categoryRepository.findById(
                    request.getParentId()
            ).orElseThrow(() ->
                    new RuntimeException(
                            "Parent category not found with id: "
                                    + request.getParentId()
                    )
            );
        }

        category.setName(request.getName());
        category.setParent(parent);

        Category updatedCategory =
                categoryRepository.save(category);

        return convertToResponse(updatedCategory);
    }

    // DELETE
    @Transactional
    public void deleteCategory(Long id) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found with id: "
                                                + id
                                )
                        );

        categoryRepository.delete(category);
    }

    private CategoryResponse convertToResponse(
            Category category) {

        Long parentId = null;
        String parentName = null;

        if (category.getParent() != null) {

            parentId = category.getParent().getId();
            parentName = category.getParent().getName();
        }

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                parentId,
                parentName,
                category.getStatus(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}