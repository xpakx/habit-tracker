package io.github.xpakx.habitgame.battle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BattleObjective {
    DEFEAT("Defeat"),
    SEIZE("Seize"),
    ESCAPE("Escape"),
    SURVIVE("Survive"),
    BOSS("Boss");

    private final String name;
}
