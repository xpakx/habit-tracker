package io.github.xpakx.habitgame.battle.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MoveAction {
    MOVE("Move"),
    ATTACK("Attack"),
    USE("Use"),
    PREPARE("Prepare");

    private final String type;
}
