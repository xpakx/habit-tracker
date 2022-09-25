package io.github.xpakx.habittracker.habit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchObjectException extends RuntimeException {
    public NoSuchObjectException(String message) {
        super(message);
    }
    public NoSuchObjectException() {
        super("Object with given ID doesn't exist!");
    }
}