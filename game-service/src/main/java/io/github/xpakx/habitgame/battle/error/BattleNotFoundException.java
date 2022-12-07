package io.github.xpakx.habitgame.battle.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BattleNotFoundException extends RuntimeException  {
    public BattleNotFoundException(String message) {
        super(message);
    }
    public BattleNotFoundException() {
        super("Not such battle!");
    }
}
