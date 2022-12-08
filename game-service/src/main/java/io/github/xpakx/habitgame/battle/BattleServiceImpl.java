package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.battle.dto.BattleResponse;
import io.github.xpakx.habitgame.battle.dto.MoveAction;
import io.github.xpakx.habitgame.battle.dto.MoveRequest;
import io.github.xpakx.habitgame.battle.dto.MoveResponse;
import io.github.xpakx.habitgame.battle.error.BattleNotFoundException;
import io.github.xpakx.habitgame.battle.error.WrongBattleStateException;
import io.github.xpakx.habitgame.battle.error.WrongPositionException;
import io.github.xpakx.habitgame.expedition.*;
import io.github.xpakx.habitgame.expedition.error.ExpeditionCompletedException;
import io.github.xpakx.habitgame.expedition.error.ExpeditionNotFoundException;
import io.github.xpakx.habitgame.expedition.error.WrongExpeditionResultType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {
    private final ExpeditionResultRepository resultRepository;
    private final BattleRepository battleRepository;
    private final ShipRepository shipRepository;
    private final PositionRepository positionRepository;

    @Override
    public BattleResponse start(Long expeditionId, Long userId) {
        ExpeditionResult result = resultRepository.findByExpeditionIdAndExpeditionUserId(expeditionId, userId).orElseThrow(ExpeditionNotFoundException::new);
        testResult(result);
        Battle battle = new Battle();
        battle.setExpedition(result.getExpedition());
        battle.setFinished(false);
        battle.setStarted(false);
        Long battleId = battleRepository.save(battle).getId();
        BattleResponse response = new BattleResponse();
        response.setBattleId(battleId);
        return response;
    }

    private void testResult(ExpeditionResult result) {
        if(result.isCompleted()) {
            throw new ExpeditionCompletedException();
        }
        if(result.getType() != ResultType.BATTLE && result.getType() != ResultType.MONSTER) {
            throw new WrongExpeditionResultType("This expedition isn't battle!");
        }
    }

    @Override
    public MoveResponse move(MoveRequest request, Long battleId, Long userId) {
        Battle battle = battleRepository.findByIdAndExpeditionUserId(battleId).orElseThrow();
        if(!battle.isStarted()) {
            throw new WrongBattleStateException("Battle hasn't started yet!");
        }
        if(battle.isFinished()) {
            throw new WrongBattleStateException("Battle is already finished!");
        }
        return null;
    }

    @Override
    @Transactional
    public MoveResponse prepare(MoveRequest request, Long battleId, Long userId) {
        if(request.getAction() != MoveAction.PREPARE) {
            throw new WrongBattleStateException("Battle is not in preparation state!");
        }
        Battle battle = battleRepository.findByIdAndExpeditionUserId(battleId).orElseThrow(BattleNotFoundException::new);
        if(battle.isStarted()) {
            throw new WrongBattleStateException("Preparation stage ended. You cannot place ships!");
        }
        if(request.getY() == null || request.getX() == null) {
            throw new WrongPositionException("Position cannot be empty!");
        }
        if(positionRepository.existsByXPosAndYPosAndBattleId(request.getX(), request.getY(), battleId)) {
            throw new WrongPositionException();
        }
        Ship ship = shipRepository.findByIdAndUserIdAndExpeditionId(request.getShipId(), userId, battle.getExpedition().getId()).orElseThrow();
        ship.setPrepared(true);
        ship = shipRepository.save(ship);
        Position position = ship.getPosition() == null ? new Position() : ship.getPosition();
        position.setShip(ship);
        position.setXPos(request.getX());
        position.setYPos(request.getY());
        positionRepository.save(position);
        MoveResponse response = new MoveResponse();
        response.setAction(MoveAction.PREPARE);
        response.setSuccess(true);
        return response;
    }
}
