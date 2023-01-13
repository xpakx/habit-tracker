package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.expedition.Ship;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BossEvaluator implements BattleResultEvaluator {
    @Override
    public boolean ofType(Battle battle) {
        return battle.getObjective().equals(BattleObjective.BOSS);
    }

    @Override
    public boolean evaluate(Battle battle, List<Ship> enemyShips) {
        return enemyShips.stream().filter(Ship::isBoss).allMatch(Ship::isDestroyed);
    }
}
