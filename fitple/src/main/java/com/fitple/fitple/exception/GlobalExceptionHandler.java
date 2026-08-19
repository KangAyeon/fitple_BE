package com.fitple.fitple.exception;

import com.fitple.fitple.dto.response.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리.
 *
 * 서비스 계층에서 던지는 예외를 아래 규칙으로 매핑한다.
 *  - IllegalArgumentException : 존재하지 않는 리소스 조회 -> 404
 *  - IllegalStateException    : 본인 소유가 아닌 리소스 수정/삭제/처리 시도 -> 403
 *  - DuplicateApplicationException : 이미 지원한 프로젝트에 중복 지원 시도 -> 409
 *  - MethodArgumentNotValidException (@Valid 검증 실패) -> 400
 *  - DataIntegrityViolationException (DB 제약조건 위반: 길이 초과, unique 중복 등) -> 400
 *  - 그 외 모든 예외 -> 500
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(IllegalArgumentException e) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(IllegalStateException e) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateApplication(DuplicateApplicationException e) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");

        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String rawMessage = e.getMostSpecificCause() != null
                ? e.getMostSpecificCause().getMessage()
                : e.getMessage();

        String userMessage;
        if (rawMessage != null && rawMessage.contains("Data too long")) {
            userMessage = "입력하신 내용이 너무 깁니다. 글자 수를 줄여서 다시 시도해주세요.";
        } else if (rawMessage != null && (rawMessage.contains("Duplicate entry") || rawMessage.contains("Duplicate"))) {
            userMessage = "이미 존재하는 값입니다. 다른 값을 입력해주세요.";
        } else if (rawMessage != null && rawMessage.contains("doesn't have a default value")) {
            userMessage = "필수 항목이 누락되었습니다. 잠시 후 다시 시도해주세요.";
        } else {
            userMessage = "요청하신 내용을 처리할 수 없습니다. 입력값을 확인해주세요.";
        }

        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(userMessage)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}