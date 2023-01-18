package io.github.xpakx.habitgame.battle.generator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.Position;
import io.github.xpakx.habitgame.expedition.Expedition;
import io.github.xpakx.habitgame.expedition.ExpeditionResult;
import io.github.xpakx.habitgame.expedition.Ship;

import java.util.List;
import java.util.Random;

public interface BattleGenerator {
    Battle createBattle(ExpeditionResult result);
    List<Ship> generateShips(Long battleId, Expedition expedition, Random random);
    List<Position> randomizePositions(List<Ship> ships, Long battleId, Random random);
}
