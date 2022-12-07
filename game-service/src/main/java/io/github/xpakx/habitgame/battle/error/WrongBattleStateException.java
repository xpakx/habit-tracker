package io.github.xpakx.habitgame.battle.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongBattleStateException extends RuntimeException  {
    public WrongBattleStateException(String message) {
        super(message);
    }
    public WrongBattleStateException() {
        super("Wrong battle state!");
    }
}
