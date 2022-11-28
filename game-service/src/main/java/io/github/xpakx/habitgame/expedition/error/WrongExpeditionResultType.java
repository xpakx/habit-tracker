package io.github.xpakx.habitgame.expedition.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongExpeditionResultType extends RuntimeException  {
    public WrongExpeditionResultType(String message) {
        super(message);
    }
    public WrongExpeditionResultType() {
        super("Wrong expedition result!");
    }
}