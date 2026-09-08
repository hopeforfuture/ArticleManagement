package com.src.categoryservice.controller;

import com.src.categoryservice.service.CategoryService;
import com.src.categoryservice.validation.CategoryRequest;
import com.src.categoryservice.dto.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    // ADMIN ONLY
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(
                categoryService.createCategory(request)
        );
    }

    // AUTHENTICATED USERS
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                categoryService.getCategory(id)
        );
    }

    // AUTHENTICATED USERS
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }

    // ADMIN ONLY
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(
                categoryService.updateCategory(id, request)
        );
    }

    // ADMIN ONLY
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.ok(
                "Category deleted successfully."
        );
    }
}