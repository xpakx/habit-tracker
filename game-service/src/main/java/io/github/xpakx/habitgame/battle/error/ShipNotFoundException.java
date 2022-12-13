package io.github.xpakx.habitgame.battle.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShipNotFoundException extends RuntimeException  {
    public ShipNotFoundException(String message) {
        super(message);
    }
    public ShipNotFoundException() {
        super("Not such ship!");
    }
}
