package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.battle.dto.BattleResponse;
import io.github.xpakx.habitgame.battle.dto.MoveRequest;
import io.github.xpakx.habitgame.battle.dto.MoveResponse;

public interface BattleService {
    BattleResponse start(Long expeditionId, Long userId);
    MoveResponse move(MoveRequest request, Long battleId, Long userId);
    MoveResponse prepare(MoveRequest request, Long battleId, Long userId);
}
