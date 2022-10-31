package io.github.xpakx.habitcity.ship.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongShipChoiceException extends RuntimeException {
    public WrongShipChoiceException(String message) {
        super(message);
    }
    public WrongShipChoiceException() {
        super("Cannot send expedition!");
    }
}