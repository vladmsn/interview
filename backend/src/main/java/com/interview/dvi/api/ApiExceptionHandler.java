package com.interview.dvi.api;

import com.interview.dvi.model.exceptions.BadRequestException;
import com.interview.dvi.model.exceptions.ConflictException;
import com.interview.dvi.model.exceptions.NotFoundException;
import com.interview.dvi.model.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

/**
 * Global exception handler for REST API.
 * Converts exceptions to appropriate HTTP responses with Problem Details.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Using RFC 9459 Problem Details for HTTP APIs ( https://www.rfc-editor.org/rfc/rfc9457.html )
     */
    private static ProblemDetail problem(HttpStatus status, String title, String detail, HttpServletRequest req) {
        var pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        if (detail != null && !detail.isBlank()) {
            pd.setDetail(detail);
        }
        pd.setInstance(URI.create(req.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Invalid request",
                ex.getMessage() == null ?  "One or more fields are invalid" : ex.getMessage(),
                req);
        pd.setProperty("errors", ex.getErrors());
        return pd;
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequestException(BadRequestException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Bad request"
                , ex.getMessage() == null ? "The request could not be understood." : ex.getMessage()
                , null);
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException ex, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, "Not found"
                , ex.getMessage() == null ? "The requested resource was not found." : ex.getMessage()
                , req);
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflictException(ConflictException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, "Conflict"
                , ex.getMessage() == null ? "The request conflicts with the current state." : ex.getMessage()
                , req);
    }

    /**
     * Generic exception handler for unexpected errors.
     * In a real-world application, might want to log these exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest req) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"
                , ex.getMessage() == null ? "An unexpected error occurred." : ex.getMessage()
                , req);
    }
}
