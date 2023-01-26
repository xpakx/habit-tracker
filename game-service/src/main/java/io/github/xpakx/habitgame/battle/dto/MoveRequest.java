package io.github.xpakx.habitgame.battle.dto;

import io.github.xpakx.habitgame.battle.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveRequest {
    Integer x;
    Integer y;
    MoveAction action;
    Long shipId;

    public Position toPosition() {
        Position position = new Position();
        position.setX(x);
        position.setY(y);
        return position;
    }
}
