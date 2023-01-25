package io.github.xpakx.habitgame.battle.distance;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.Position;

import java.util.List;

public interface DistanceEvaluator {
    int shortestPath(List<Position> positions, Position startPosition, Position targetPosition, Battle battle);
}
