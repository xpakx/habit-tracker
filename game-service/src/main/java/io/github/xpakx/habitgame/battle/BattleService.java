package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.battle.dto.BattleResponse;
import io.github.xpakx.habitgame.battle.dto.MoveResponse;

public interface BattleService {
    BattleResponse start(Long expeditionId, Long userId);
    MoveResponse move(MoveResponse request, Long battleId, Long userId);
    MoveResponse prepare(MoveResponse request, Long battleId, Long userId);
}
