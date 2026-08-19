package com.fitple.fitple.exception;

/**
 * 이미 지원한(PENDING 또는 ACCEPTED) 프로젝트에 다시 지원하려고 할 때 던진다.
 * GlobalExceptionHandler에서 409 Conflict로 매핑된다.
 */
public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException(String message) {
        super(message);
    }
}