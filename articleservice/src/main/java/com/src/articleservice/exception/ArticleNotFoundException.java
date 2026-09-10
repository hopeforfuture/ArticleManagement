package com.src.articleservice.exception;

public class ArticleNotFoundException
        extends RuntimeException {

    public ArticleNotFoundException(String message) {
        super(message);
    }
}