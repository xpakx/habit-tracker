package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.battle.dto.BattleResponse;
import io.github.xpakx.habitgame.battle.dto.MoveRequest;
import io.github.xpakx.habitgame.battle.dto.MoveResponse;
import io.github.xpakx.habitgame.expedition.ExpeditionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {
    private final ExpeditionResultRepository resultRepository;

    @Override
    public BattleResponse start(Long expeditionId, Long userId) {
        return null;
    }

    @Override
    public MoveResponse move(MoveRequest request, Long battleId, Long userId) {
        return null;
    }

    @Override
    public MoveResponse prepare(MoveRequest request, Long battleId, Long userId) {
        return null;
    }
}
