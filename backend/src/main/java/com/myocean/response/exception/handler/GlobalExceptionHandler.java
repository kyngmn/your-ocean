package com.myocean.response.exception.handler;

import com.myocean.response.ApiResponse;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException ex) {
        log.error("Error Code: {}, Message: {}, Data: {}",
                ex.getErrorStatus().getCode(),
                ex.getErrorStatus().getMessage(),
                ex.getData() != null ? ex.getData() : "No additional data",
                ex
        );

        return ResponseEntity
                .status(ex.getErrorStatus().getHttpStatus())
                .body(ApiResponse.onFailure(
                        ex.getErrorStatus(),
                        ex.getData()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        List<String> errorMessages = getValidationErrorMessages(ex);
        log.error("Validation errors: {}", errorMessages, ex);

        return ResponseEntity
                .status(ErrorStatus.BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.onFailure(
                        ErrorStatus.BAD_REQUEST,
                        errorMessages
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex
    ) {
        List<String> errorMessages = getValidationErrorMessages(ex);
        log.error("Validation errors: {}", errorMessages, ex);

        return ResponseEntity
                .status(ErrorStatus.BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.onFailure(
                        ErrorStatus.BAD_REQUEST,
                        errorMessages
                ));
    }

    private List<String> getValidationErrorMessages(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    String field = fieldError.getField();       // 실패한 필드 이름
                    String message = fieldError.getDefaultMessage(); // 검증 실패 메시지
                    return field + ": " + message;
                })
                .toList();
    }

    private List<String> getValidationErrorMessages(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(violation -> {
                    String field = violation.getPropertyPath().toString(); // 위반된 필드
                    String message = violation.getMessage();              // 검증 실패 메시지
                    return field + ": " + message;
                })
                .toList();
    }

    // Spring Security 인증 관련 예외 처리
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication failed: {}", ex.getMessage(), ex);

        ErrorStatus errorStatus = ErrorStatus.LOGIN_REQUIRED;

        // 세부적인 인증 예외 타입에 따라 다른 메시지 제공
        if (ex instanceof BadCredentialsException) {
            errorStatus = ErrorStatus.TOKEN_INVALID;
        } else if (ex instanceof InsufficientAuthenticationException) {
            errorStatus = ErrorStatus.LOGIN_REQUIRED;
        } else if (ex instanceof AuthenticationCredentialsNotFoundException) {
            errorStatus = ErrorStatus.LOGIN_REQUIRED;
        }

        return ResponseEntity
                .status(errorStatus.getHttpStatus())
                .body(ApiResponse.onFailure(errorStatus, null));
    }

    // Spring Security 인가 관련 예외 처리 (권한 부족)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(ErrorStatus.ACCESS_DENIED.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorStatus.ACCESS_DENIED, null));
    }

    // 일반적인 예외 처리 (최후의 보루)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralServerException(Exception ex) {
        log.error("Unexpected server error occurred: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR, null));
    }
}
