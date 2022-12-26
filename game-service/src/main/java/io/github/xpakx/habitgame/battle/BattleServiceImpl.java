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
import io.github.xpakx.habitgame.ship.ShipType;
import io.github.xpakx.habitgame.ship.ShipTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {
    private final ExpeditionResultRepository resultRepository;
    private final BattleRepository battleRepository;
    private final ShipRepository shipRepository;
    private final PositionRepository positionRepository;
    private final ShipTypeRepository shipTypeRepository;

    @Override
    public BattleResponse start(Long expeditionId, Long userId) {
        ExpeditionResult result = resultRepository.findByExpeditionIdAndExpeditionUserId(expeditionId, userId).orElseThrow(ExpeditionNotFoundException::new);
        testResult(result);
        Battle battle = new Battle();
        battle.setExpedition(result.getExpedition());
        battle.setFinished(false);
        battle.setStarted(false);
        battle.setHeight(15);
        battle.setWidth(20);
        battle.setObjective(BattleObjective.DEFEAT);
        battle.setTurn(0);
        Long battleId = battleRepository.save(battle).getId();
        BattleResponse response = new BattleResponse();
        generateEnemyShips(battleId, result.getExpedition());
        response.setBattleId(battleId);
        return response;
    }

    private void generateEnemyShips(Long battleId, Expedition expedition) {
        Random random = new Random();
        List<Integer> rarities = shipRepository.findByExpeditionId(expedition.getId()).stream()
                .map(Ship::getRarity)
                .toList();
        List<Ship> shipsToAdd = generateShips(expedition, random, rarities, getShipTypes(rarities));
        positionRepository.saveAll(randomizePositions(shipRepository.saveAll(shipsToAdd), battleId, random));
    }

    private List<Position> randomizePositions(List<Ship> ships, Long battleId, Random random) {
        int boardWidth = 20;
        int boardHeight = 15;
        List<Position> positions = new ArrayList<>();
        for(int i = 0; i < boardWidth/2; i++) {
            for(int j = 0; j < boardHeight; j++) {
                Position pos = new Position();
                pos.setX(i);
                pos.setY(j);
                positions.add(pos);
            }
        }
        Collections.shuffle(positions);
        List<Position> result = new ArrayList<>();
        int positionIndex = 0;
        for(Ship ship : ships) {
            Position position = positions.get(positionIndex);
            position.setBattle(battleRepository.getReferenceById(battleId));
            position.setShip(ship);
            result.add(position);
        }
        return result;
    }

    private List<Ship> generateShips(Expedition expedition, Random random, List<Integer> rarities, List<ShipType> shipPrototypes) {
        List<Ship> shipsToAdd = new ArrayList<>();
        System.out.println("Prototypes: " + shipPrototypes.size());
        for(ShipType prototype : shipPrototypes) {
            long ships = calculateShipCount(random, rarities, prototype);
            System.out.println("Rarity: " +  prototype.getRarity() + ", Ships: " + ships);
            for(long i = ships; i>0; i--) {
                shipsToAdd.add(generateShipFromPrototype(expedition, prototype, random.nextInt(2)-1));
            }
        }
        return shipsToAdd;
    }

    private long calculateShipCount(Random random, List<Integer> rarities, ShipType prototype) {
        long rarityCount = rarities.stream().filter((a) -> Objects.equals(a, prototype.getRarity())).count();
        long shipBonus = rarityCount > 1 ? random.nextLong((long) (0.2*rarityCount)) - (long) (0.1*rarityCount) : 0;
        return rarityCount + shipBonus;
    }

    private List<ShipType> getShipTypes(List<Integer> rarities) {
        List<Integer> distinctRarities = rarities.stream().distinct().toList();
        List<ShipType> shipPrototypes = new ArrayList<>();
        for(Integer rarity : distinctRarities) {
            System.out.println("Current rarity: " + rarity);
            shipPrototypes.addAll(shipTypeRepository.findRandomTypes(1, rarity));
        }
        return shipPrototypes;
    }

    private Ship generateShipFromPrototype(Expedition expedition, ShipType prototype, Integer sizeBonus) {
        Ship ship = new Ship();
        ship.setPrepared(true);
        ship.setDestroyed(false);
        ship.setCode(prototype.getCode());
        ship.setName(prototype.getName());
        ship.setSize(prototype.getBaseSize()+sizeBonus);
        ship.setExpedition(expedition);
        ship.setDamaged(false);
        ship.setDestroyed(false);
        ship.setPrepared(false);
        ship.setAction(false);
        ship.setMovement(false);
        ship.setEnemy(true);
        ship.setUserId(expedition.getUserId());
        return ship;
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
        Ship ship = shipRepository.findByIdAndUserIdAndExpeditionId(request.getShipId(), userId, battle.getExpedition().getId()).orElseThrow(ShipNotFoundException::new);
        testShipOwnership(ship);
        testShipHealth(ship);
        if(request.getAction() == MoveAction.MOVE) {
            makeMove(request, battleId, ship);
        } else if(request.getAction() == MoveAction.ATTACK) {
            attack(request, battleId, ship);
        } else if(request.getAction() == MoveAction.USE) {
            use(request, battleId, ship);
        } else {
            throw new WrongMoveException("Action type is incorrect!");
        }
        return prepareMoveResponse(request.getAction());
    }

    private void testShipHealth(Ship ship) {
        if(ship.isDestroyed()) {
            throw new WrongMoveException("Selected ship is destroyed!");
        }
    }

    private void testShipOwnership(Ship ship) {
        if(ship.isEnemy()) {
            throw new WrongMoveException("You can't move enemy ships!");
        }
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
        attackedShip.setSize(attackedShip.getSize()-1);
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
            throw new WrongMoveException("Action type is incorrect!");
        }
    }

    private Ship saveShip(MoveRequest request, Long userId, Battle battle) {
        Ship ship = shipRepository.findByIdAndUserIdAndExpeditionId(request.getShipId(), userId, battle.getExpedition().getId()).orElseThrow(ShipNotFoundException::new);
        testShipOwnership(ship);
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
        Battle battle = battleRepository.findByIdAndExpeditionUserId(battleId, userId).orElseThrow(BattleNotFoundException::new);
        if(battle.isFinished()) {
            throw new WrongBattleStateException("Battle is already finished!");
        }

        List<Ship> ships = shipRepository.findByExpeditionIdAndEnemyFalse(battle.getExpedition().getId());
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
