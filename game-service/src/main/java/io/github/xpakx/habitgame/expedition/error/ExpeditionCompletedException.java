package io.github.xpakx.habitgame.expedition.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExpeditionCompletedException extends RuntimeException  {
    public ExpeditionCompletedException(String message) {
        super(message);
    }
    public ExpeditionCompletedException() {
        super("Expedition completed!");
    }
}
