package com.src.commentservice.exception;

public class ArticleNotPublishedException
        extends RuntimeException {

    public ArticleNotPublishedException(
            String message) {

        super(message);
    }
}