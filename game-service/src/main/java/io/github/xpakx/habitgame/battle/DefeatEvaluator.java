package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.expedition.Ship;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefeatEvaluator implements BattleResultEvaluator {
    @Override
    public boolean ofType(Battle battle) {
        return battle.getObjective().equals(BattleObjective.DEFEAT);
    }

    @Override
    public boolean evaluate(Battle battle, List<Ship> enemyShips) {
        return enemyShips.stream().allMatch(Ship::isDestroyed);
    }
}
