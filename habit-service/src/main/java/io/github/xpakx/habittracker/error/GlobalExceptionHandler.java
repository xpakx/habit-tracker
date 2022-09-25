package io.github.xpakx.habittracker.error;

import io.github.xpakx.habittracker.error.dto.ErrorResponse;
import io.github.xpakx.habittracker.habit.error.NoSuchObjectException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { NoSuchObjectException.class })
    protected ResponseEntity<Object> handleNoSuchObjectException(RuntimeException ex, WebRequest request) {
        ErrorResponse errorBody = new ErrorResponse();
        errorBody.setMessage(ex.getMessage());
        errorBody.setStatus(HttpStatus.NOT_FOUND);
        return handleExceptionInternal(
                ex,
                errorBody,
                new HttpHeaders(),
                errorBody.getStatus(),
                request
        );
    }
}
