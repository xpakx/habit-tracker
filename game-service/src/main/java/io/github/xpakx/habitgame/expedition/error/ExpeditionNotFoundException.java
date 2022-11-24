package io.github.xpakx.habitgame.expedition.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExpeditionNotFoundException extends RuntimeException  {
    public ExpeditionNotFoundException(String message) {
        super(message);
    }
    public ExpeditionNotFoundException() {
        super("Expedition not found!");
    }
}