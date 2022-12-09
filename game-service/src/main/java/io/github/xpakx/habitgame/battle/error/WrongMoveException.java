package io.github.xpakx.habitgame.battle.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongMoveException extends RuntimeException  {
    public WrongMoveException(String message) {
        super(message);
    }
    public WrongMoveException() {
        super("Wrong move!");
    }
}
