package io.github.xpakx.habitgame.battle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveRequest {
    Integer x;
    Integer y;
    MoveAction action;
    Long shipId;
}
