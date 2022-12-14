package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.battle.dto.BattleResponse;
import io.github.xpakx.habitgame.battle.dto.MoveAction;
import io.github.xpakx.habitgame.battle.dto.MoveRequest;
import io.github.xpakx.habitgame.battle.dto.MoveResponse;
import io.github.xpakx.habitgame.battle.error.*;
import io.github.xpakx.habitgame.expedition.*;
import io.github.xpakx.habitgame.expedition.error.ExpeditionCompletedException;
import io.github.xpakx.habitgame.expedition.error.ExpeditionNotFoundException;
import io.github.xpakx.habitgame.expedition.error.WrongExpeditionResultType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    @Transactional
    public MoveResponse move(MoveRequest request, Long battleId, Long userId) {
        Battle battle = battleRepository.findByIdAndExpeditionUserId(battleId, userId).orElseThrow(BattleNotFoundException::new);
        testShipMove(request, battleId, battle);
        Ship ship = shipRepository.findByIdAndUserIdAndExpeditionId(request.getShipId(), userId, battle.getExpedition().getId()).orElseThrow();
        if(request.getAction() == MoveAction.MOVE) {
            makeMove(request, battleId, ship);
        } else if(request.getAction() == MoveAction.ATTACK) {
            attack(request, battleId, ship);
        } else if(request.getAction() == MoveAction.USE) {
            use(request, battleId, ship);
        }
        return prepareMoveResponse(request.getAction());
    }

    private void makeMove(MoveRequest request, Long battleId, Ship ship) {
        testNewPosition(request, battleId);
        testMove(ship, request, battleId);
        Position position = ship.getPosition();
        position.setX(request.getX());
        position.setY(request.getY());
        positionRepository.save(position);
        shipRepository.updateMovementById(ship.getId());
    }

    private void testMove(Ship ship, MoveRequest request, Long battleId) {
        if(ship.isMovement()) {
            throw new WrongMoveException("Ship already moved!");
        }
        // List<Position> positions = positionRepository.findByBattleId(battleId);
        if(taxiLength(ship.getPosition().getX(), ship.getPosition().getY(), request.getX(), request.getY()) > 3) {
            throw new WrongMoveException("Your move is too long!");
        }
    }

    private int taxiLength(Integer x1, Integer y1, Integer x2, Integer y2) {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }

    private void attack(MoveRequest request, Long battleId, Ship ship) {
        Position position = positionRepository.findByXAndYAndBattleId(request.getX(), request.getY(), battleId).orElseThrow(WrongPositionException::new);
        if(position.getShip() == null) {
            throw new WrongMoveException("Nothing to attack!");
        }
        if(taxiLength(ship.getPosition().getX(), ship.getPosition().getY(), request.getX(), request.getY()) > 3) {
            throw new WrongMoveException("Target is too far away!");
        }
        if(ship.isAction()) {
            throw new WrongMoveException("Ship already made an action!");
        }
        Ship attackedShip = position.getShip();
        attackedShip.setDamaged(true);
        attackedShip.setSize(ship.getSize()-1);
        if(attackedShip.getSize() <= 0) {
            attackedShip.setDestroyed(true);
            attackedShip.setPosition(null);
        }
        shipRepository.save(attackedShip);
        shipRepository.updateActionById(ship.getId());
    }

    private void use(MoveRequest request, Long battleId, Ship ship) {
        // TODO
    }

    private void testShipMove(MoveRequest request, Long battleId, Battle battle) {
        if(!battle.isStarted()) {
            throw new WrongBattleStateException("Battle hasn't started yet!");
        }
        if(battle.isFinished()) {
            throw new WrongBattleStateException("Battle is already finished!");
        }
        testNewPosition(request, battleId);
    }

    @Override
    @Transactional
    public MoveResponse prepare(MoveRequest request, Long battleId, Long userId) {
        testActionType(request, MoveAction.PREPARE);
        Battle battle = battleRepository.findByIdAndExpeditionUserId(battleId, userId).orElseThrow(BattleNotFoundException::new);
        testShipPlacement(request, battleId, battle);
        Ship ship = saveShip(request, userId, battle);
        savePosition(request, ship);
        return prepareMoveResponse(MoveAction.PREPARE);
    }

    private void testShipPlacement(MoveRequest request, Long battleId, Battle battle) {
        if(battle.isStarted()) {
            throw new WrongBattleStateException("Preparation stage ended. You cannot place ships!");
        }
        testNewPosition(request, battleId);
    }

    private void testNewPosition(MoveRequest request, Long battleId) {
        if(request.getY() == null || request.getX() == null) {
            throw new WrongPositionException("Position cannot be empty!");
        }
        if(positionRepository.existsByXAndYAndBattleId(request.getX(), request.getY(), battleId)) {
            throw new WrongPositionException();
        }
    }

    private void testActionType(MoveRequest request, MoveAction action) {
        if(request.getAction() != action) {
            throw new WrongBattleStateException("Battle is not in preparation state!");
        }
    }

    private Ship saveShip(MoveRequest request, Long userId, Battle battle) {
        Ship ship = shipRepository.findByIdAndUserIdAndExpeditionId(request.getShipId(), userId, battle.getExpedition().getId()).orElseThrow(ShipNotFoundException::new);
        ship.setPrepared(true);
        ship = shipRepository.save(ship);
        return ship;
    }

    private void savePosition(MoveRequest request, Ship ship) {
        Position position = ship.getPosition() == null ? new Position() : ship.getPosition();
        position.setShip(ship);
        position.setX(request.getX());
        position.setY(request.getY());
        positionRepository.save(position);
    }

    private MoveResponse prepareMoveResponse(MoveAction action) {
        MoveResponse response = new MoveResponse();
        response.setAction(action);
        response.setSuccess(true);
        return response;
    }

    @Override
    public List<MoveResponse> endTurn(Long battleId, Long userId) {
        Battle battle = battleRepository.findByIdAndExpeditionUserId(battleId, userId).orElseThrow();
        if(battle.isFinished()) {
            throw new WrongBattleStateException("Battle is already finished!");
        }

        List<Ship> ships = shipRepository.findByExpeditionId(battle.getExpedition().getId());
        if(battle.isStarted()) {
            for(Ship ship : ships) {
                ship.setMovement(false);
                ship.setAction(false);
            }
            shipRepository.saveAll(ships);
        } else {
            if(allPrepared(ships)) {
                throw new WrongMoveException("Not all ships are placed!");
            }
            battle.setStarted(true);
            battleRepository.save(battle);
        }

        return new ArrayList<>();
    }

    private boolean allPrepared(List<Ship> ships) {
        return ships.stream().anyMatch((s) -> !s.isPrepared());
    }
}
