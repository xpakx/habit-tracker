package io.github.xpakx.habitgame.battle.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PreparationStageEndedException extends RuntimeException  {
    public PreparationStageEndedException(String message) {
        super(message);
    }
    public PreparationStageEndedException() {
        super("Preparation stage ended. You cannot place ships!");
    }
}
