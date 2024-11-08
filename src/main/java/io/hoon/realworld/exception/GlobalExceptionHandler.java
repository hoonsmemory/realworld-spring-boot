package io.hoon.realworld.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handle(IllegalArgumentException e) {
        log.info(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    ProblemDetail handle(NoSuchElementException e) {
        log.info(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail handle(AccessDeniedException e) {
        log.info(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail handle(AuthenticationException e) {
        log.info(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.info(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "A unique constraint or primary key constraint has been violated due to duplicate data.");
    }

    /**
     * Errors that the developer did not expect are handled here and the log level is recorded as
     * error.
     *
     * @param e Exception
     * @return ProblemDetail
     */
    @ExceptionHandler(Exception.class)
    ProblemDetail handle(Exception e) {
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Please contact the administrator.");
    }
}