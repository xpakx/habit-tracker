package io.github.xpakx.habittracker.error;

import io.github.xpakx.habittracker.error.dto.ErrorResponse;
import io.github.xpakx.habittracker.habit.error.NoSuchObjectException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NoSuchObjectException.class)
    protected ResponseEntity<Object> handleNoSuchObjectException(RuntimeException ex, WebRequest request) {
        HttpStatus status = getStatus(ex);
        return handleExceptionInternal(
                ex,
                constructErrorBody(ex, status),
                new HttpHeaders(),
                status,
                request
        );
    }

    private ErrorResponse constructErrorBody(RuntimeException ex, HttpStatus status) {
        ErrorResponse errorBody = new ErrorResponse();
        errorBody.setMessage(ex.getMessage());
        errorBody.setStatus(status.getReasonPhrase());
        errorBody.setError(status.value());
        return errorBody;
    }

    private HttpStatus getStatus(RuntimeException ex) {
        ResponseStatus status = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
        return status != null ? status.code() : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
