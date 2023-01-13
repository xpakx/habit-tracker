package io.github.xpakx.habitgame.battle.evaluator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.expedition.Ship;

import java.util.List;

public interface BattleResultEvaluator {
    boolean ofType(Battle battle);
    boolean evaluate(Battle battle, List<Ship> enemyShips);
}
