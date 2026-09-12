package com.src.commentservice.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =====================================================
    // COMMENT NOT FOUND
    // =====================================================

    @ExceptionHandler(
            CommentNotFoundException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleCommentNotFound(
            CommentNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    // =====================================================
    // INVALID COMMENT
    // =====================================================

    @ExceptionHandler(
            InvalidCommentException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleInvalidComment(
            InvalidCommentException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // =====================================================
    // ARTICLE NOT PUBLISHED
    // =====================================================

    @ExceptionHandler(
            ArticleNotPublishedException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleArticleNotPublished(
            ArticleNotPublishedException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // =====================================================
    // VALIDATION
    // =====================================================

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors =
                new HashMap<>();

        for (FieldError error :
                ex.getBindingResult()
                        .getFieldErrors()) {

            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "status",
                HttpStatus.BAD_REQUEST.value()
        );

        response.put(
                "error",
                "Validation Failed"
        );

        response.put(
                "message",
                errors
        );

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // =====================================================
    // ANY OTHER EXCEPTION
    // =====================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleException(Exception ex) {

        ex.printStackTrace();

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
    }

    // =====================================================
    // COMMON RESPONSE
    // =====================================================

    private ResponseEntity<Map<String, Object>>
    buildResponse(
            HttpStatus status,
            String message) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "status",
                status.value()
        );

        response.put(
                "error",
                status.getReasonPhrase()
        );

        response.put(
                "message",
                message
        );

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}