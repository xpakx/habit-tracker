package io.github.xpakx.habitgame.expedition.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExpeditionNotFinishedException extends RuntimeException  {
    public ExpeditionNotFinishedException(String message) {
        super(message);
    }
    public ExpeditionNotFinishedException() {
        super("Expedition not finished!");
    }
}
