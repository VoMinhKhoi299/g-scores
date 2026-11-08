package vn.gscores.web;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.gscores.dto.ApiErrorResponse;
import vn.gscores.exception.ResourceNotFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Chuan hoa moi loi ra cung mot dinh dang JSON: status, error, message, timestamp
 * va (neu co) danh sach fieldErrors.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    /**
     * Loi validate body request (@Valid tren DTO). MethodArgumentNotValidException
     * extends BindException, nen handler nay cung bat duoc loi binding cua
     * @ModelAttribute.
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiErrorResponse> handleBindingError(BindException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Du lieu request khong hop le", fieldErrors);
    }

    /**
     * Loi validate path variable / query param khi dung co che AOP proxy
     * (@Validated tren controller class).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                fieldErrors.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage()));
        return build(HttpStatus.BAD_REQUEST, "Du lieu request khong hop le", fieldErrors);
    }

    /**
     * Spring 6.1+ co co che validation method noi bo, nem ra
     * HandlerMethodValidationException thay vi ConstraintViolationException.
     * Handler nay dam bao ca hai duong deu tra ve cung dinh dang loi.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodValidation(HandlerMethodValidationException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (var result : ex.getParameterValidationResults()) {
            String name = result.getMethodParameter().getParameterName();
            for (var error : result.getResolvableErrors()) {
                fieldErrors.putIfAbsent(name, error.getDefaultMessage());
            }
        }
        return build(HttpStatus.BAD_REQUEST, "Du lieu request khong hop le", fieldErrors);
    }

    /**
     * Giu nguyen status goc cua Spring (404 khi sai duong dan, 415 khi sai content-type...).
     * Handler Exception tong quat ben duoi se bien cac loi nay thanh 500 neu thieu handler nay.
     */
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleSpringError(ErrorResponseException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return build(status, ex.getMessage(), Map.of());
    }

    /**
     * Sai HTTP method (POST len endpoint chi nhan GET) — Spring nem
     * HttpRequestMethodNotSupportedException, khong phai ErrorResponseException.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method khong duoc ho tro: " + ex.getMethod(), Map.of());
    }

    /**
     * Khong tim thay resource (duong dan khong ton tai).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Khong tim thay endpoint: " + ex.getResourcePath(), Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        log.error("Loi khong mong doi", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Da co loi xay ra, vui long thu lai sau", Map.of());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, Map<String, String> fieldErrors) {
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                Instant.now(),
                fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
