package vn.gscores.exception;

/**
 * Thieu lieu khong ton tai trong database. Duoc map thanh HTTP 404 boi
 * {@link vn.gscores.web.GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
