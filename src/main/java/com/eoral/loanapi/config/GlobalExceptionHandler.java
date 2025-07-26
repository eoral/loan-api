package com.eoral.loanapi.config;

import com.eoral.loanapi.dto.ErrorResponse;
import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.exception.ForbiddenException;
import com.eoral.loanapi.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) {
        // Compared to other handlers, we don't return original error message here on purpose.
        UUID uuid = UUID.randomUUID();
        String message = "Unknown error " + uuid;
        log.error(message, exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(message));
    }
}
