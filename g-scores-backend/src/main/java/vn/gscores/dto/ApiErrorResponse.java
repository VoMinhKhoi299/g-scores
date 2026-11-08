package vn.gscores.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Dinh dang loi chung cho moi truong hop 4xx/5xx.
 */
public record ApiErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        Map<String, String> fieldErrors) {
}
