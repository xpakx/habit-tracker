package io.github.xpakx.habitgame.battle.evaluator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.BattleObjective;
import io.github.xpakx.habitgame.expedition.Ship;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeizeEvaluator implements BattleResultEvaluator {
    @Override
    public boolean ofType(Battle battle) {
        return battle.getObjective().equals(BattleObjective.SEIZE);
    }

    @Override
    public boolean evaluate(Battle battle, List<Ship> enemyShips) {
        return battle.isSeized();
    }
}
