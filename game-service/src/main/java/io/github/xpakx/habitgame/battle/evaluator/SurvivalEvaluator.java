package io.github.xpakx.habitgame.battle.evaluator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.BattleObjective;
import io.github.xpakx.habitgame.expedition.Ship;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurvivalEvaluator implements BattleResultEvaluator {
    @Override
    public boolean ofType(Battle battle) {
        return battle.getObjective().equals(BattleObjective.SURVIVE);
    }

    @Override
    public boolean evaluate(Battle battle, List<Ship> enemyShips) {
        return battle.getTurn() == 10;
    }
}
