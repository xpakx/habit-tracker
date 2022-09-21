package io.github.xpakx.habitcity.city.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemRequirementsNotMetException extends RuntimeException {
    public ItemRequirementsNotMetException(String message) {
        super(message);
    }
    public ItemRequirementsNotMetException() {
        super("Requirements for given item not met!");
    }
}