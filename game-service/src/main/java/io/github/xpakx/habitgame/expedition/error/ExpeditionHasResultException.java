package io.github.xpakx.habitgame.expedition.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExpeditionHasResultException extends RuntimeException  {
    public ExpeditionHasResultException(String message) {
        super(message);
    }
    public ExpeditionHasResultException() {
        super("Expedition already has result!");
    }
}
