package com.hc.stackoverflow.exception;

public class QuestionCreationException extends RuntimeException {
    public QuestionCreationException(String message) {
        super(message);
    }
}
