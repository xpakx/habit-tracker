package io.github.xpakx.habitcity.ship.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotAShipException extends RuntimeException {
    public NotAShipException(String message) {
        super(message);
    }
    public NotAShipException() {
        super("Not a ship!");
    }
}