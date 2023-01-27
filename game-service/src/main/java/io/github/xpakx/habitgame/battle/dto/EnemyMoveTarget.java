package io.github.xpakx.habitgame.battle.dto;

import io.github.xpakx.habitgame.battle.Position;
import io.github.xpakx.habitgame.expedition.Ship;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EnemyMoveTarget {
    private final Position position;
    private final Ship target;
}
