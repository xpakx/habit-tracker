package io.github.xpakx.habitgame.battle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveResponse {
    MoveAction action;
    boolean success;
}
