package io.github.xpakx.habitgame.battle.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongPositionException extends RuntimeException  {
    public WrongPositionException(String message) {
        super(message);
    }
    public WrongPositionException() {
        super("Wrong position!");
    }
}
