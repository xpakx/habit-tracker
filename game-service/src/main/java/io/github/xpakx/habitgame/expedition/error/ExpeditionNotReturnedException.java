package io.github.xpakx.habitgame.expedition.error;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExpeditionNotReturnedException extends RuntimeException  {
    public ExpeditionNotReturnedException(String message) {
        super(message);
    }
    public ExpeditionNotReturnedException() {
        super("Expedition not returned!");
    }
}
