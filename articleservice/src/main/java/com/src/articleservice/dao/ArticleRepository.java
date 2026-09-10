package com.src.articleservice.dao;

import com.src.articleservice.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository
        extends JpaRepository<Article, Long> {

    List<Article> findAllByOrderByIdDesc();

    List<Article> findByUserIdOrderByIdDesc(Long userId);

    List<Article> findByIsPublishedTrueOrderByIdDesc();
}