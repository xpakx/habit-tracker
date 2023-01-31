package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.battle.distance.DistanceEvaluator;
import io.github.xpakx.habitgame.battle.dto.*;
import io.github.xpakx.habitgame.battle.error.*;
import io.github.xpakx.habitgame.battle.evaluator.BattleResultEvaluator;
import io.github.xpakx.habitgame.battle.generator.BattleGenerator;
import io.github.xpakx.habitgame.expedition.*;
import io.github.xpakx.habitgame.expedition.error.ExpeditionCompletedException;
import io.github.xpakx.habitgame.expedition.error.ExpeditionNotFoundException;
import io.github.xpakx.habitgame.expedition.error.WrongExpeditionResultType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {
    private static final int CRITICAL_MULTI = 3;
    private final ExpeditionResultRepository resultRepository;
    private final BattleRepository battleRepository;
    private final ShipRepository shipRepository;
    private final PositionRepository positionRepository;
    private final List<BattleResultEvaluator> resultEvaluators;
    private final List<BattleGenerator> battleGenerators;
    private final DistanceEvaluator distanceEvaluator;

    @Override
    public BattleResponse getBattle(Long expeditionId, Long userId) {
        Optional<Battle> battle = battleRepository.findByExpeditionIdAndExpeditionUserId(expeditionId, userId);
        return battle.map(this::getBattleResponse).orElseGet(() -> startBattle(expeditionId, userId));
    }

    private BattleResponse getBattleResponse(Battle battle) {
        BattleResponse response = new BattleResponse();
        response.setBattleId(battle.getId());
        response.setWidth(battle.getWidth());
        response.setHeight(battle.getHeight());
        response.setFinished(battle.isFinished());
        response.setStarted(battle.isStarted());
        response.setObjective(battle.getObjective());
        response.setTurn(battle.getTurn());
        List<Ship> ships = shipRepository.findByExpeditionId(battle.getExpedition().getId());
        response.setShips(filterPlayerShips(ships).map(this::toShipResponse).toList());
        response.setEnemyShips(filterEnemyShips(ships).map(this::toShipResponse).toList());
        return response;
    }

    private BattleShip toShipResponse(Ship ship) {
        BattleShip battleShip = new BattleShip();
        battleShip.setId(ship.getId());
        battleShip.setShipId(ship.getShipId());
        battleShip.setName(ship.getName());
        battleShip.setCode(ship.getCode());
        battleShip.setRarity(ship.getRarity());
        battleShip.setSize(ship.getSize());
        battleShip.setHp(ship.getHp());
        battleShip.setStrength(ship.getStrength());
        battleShip.setHitRate(ship.getHitRate());
        battleShip.setCriticalRate(ship.getCriticalRate());
        battleShip.setDestroyed(ship.isDestroyed());
        battleShip.setDamaged(ship.isDamaged());
        battleShip.setPrepared(ship.isPrepared());
        battleShip.setAction(ship.isAction());
        battleShip.setMovement(ship.isMovement());
        battleShip.setEnemy(ship.isEnemy());
        if(ship.getPosition() != null) {
            BattlePosition position = new BattlePosition();
            position.setX(ship.getPosition().getX());
            position.setY(ship.getPosition().getX());
            battleShip.setPosition(position);
        }
        return battleShip;
    }

    private Stream<Ship> filterEnemyShips(List<Ship> ships) {
        return ships.stream().filter(Ship::isEnemy);
    }

    private Stream<Ship> filterPlayerShips(List<Ship> ships) {
        return ships.stream().filter((a) -> !a.isEnemy());
    }

    private BattleResponse startBattle(Long expeditionId, Long userId) {
        ExpeditionResult result = resultRepository.findByExpeditionIdAndExpeditionUserId(expeditionId, userId).orElseThrow(ExpeditionNotFoundException::new);
        testResult(result);
        BattleGenerator generator  = randomizeObjective();
        Battle battle = battleRepository.save(generator.createBattle(result));
        Random random = new Random();
        List<Ship> ships = shipRepository.saveAll(generator.generateShips(battle.getId(), result.getExpedition(), random));
        positionRepository.saveAll(generator.randomizePositions(ships, battle, random));
        return getBattleResponse(battle);
    }

    private BattleGenerator randomizeObjective() {
        Random random = new Random();
        return battleGenerators.get(random.nextInt(battleGenerators.size()));
    }

    private void testResult(ExpeditionResult result) {
        if(result.isCompleted()) {
            throw new ExpeditionCompletedException();
        }
        if(result.getType() != ResultType.BATTLE) {
            throw new WrongExpeditionResultType("This expedition isn't battle!");
        }
    }

    @Override
    @Transactional
    public MoveResponse move(MoveRequest request, Long battleId, Long userId) {
        Battle battle = battleRepository.findByIdAndExpeditionUserId(battleId, userId).orElseThrow(BattleNotFoundException::new);
        testShipMove(battle);
        Ship ship = shipRepository.findByIdAndUserIdAndExpeditionId(request.getShipId(), userId, battle.getExpedition().getId()).orElseThrow(ShipNotFoundException::new);
        testShipOwnership(ship);
        testShipHealth(ship);
        MoveResult moveResult = null;
        AttackResult attackResult = null;
        if(request.getAction() == MoveAction.MOVE) {
            moveResult = makeMove(request, battle, ship);
        } else if(request.getAction() == MoveAction.ATTACK) {
            attackResult = attack(request, battleId, ship);
        } else if(request.getAction() == MoveAction.USE) {
            use(request, battleId, ship);
        } else {
            throw new WrongMoveException("Action type is incorrect!");
        }
        return prepareMoveResponse(request.getAction(), moveResult, attackResult);
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

    private MoveResult makeMove(MoveRequest request, Battle battle, Ship ship) {
        Optional<Position> newPosition = testNewPosition(request, battle.getId());
        testMove(ship, request, battle);
        savePosition(request, ship, newPosition);
        shipRepository.updateMovementById(ship.getId());
        MoveResult result = new MoveResult();
        result.setShipId(ship.getShipId());
        result.setX(result.getX());
        result.setY(result.getY());
        return result;
    }

    private void testMove(Ship ship, MoveRequest request, Battle battle) {
        if(ship.isMovement()) {
            throw new WrongMoveException("Ship already moved!");
        }
        if(request.getX() < 0 || request.getY() < 0 || request.getX() > battle.getWidth() || request.getY() > battle.getHeight()) {
            throw new WrongMoveException("Position is outside the board!");
        }
        if(taxiLength(ship.getPosition().getX(), ship.getPosition().getY(), request.getX(), request.getY()) > ship.getMovementRange()) {
            throw new WrongMoveException("Your move is too long!");
        }
        List<Position> positions = positionRepository.findByBattleId(battle.getId());
        int realLength = distanceEvaluator.shortestPath(positions, ship.getPosition(), request.toPosition(), battle);
        if(realLength == -1) {
            throw new WrongMoveException("Target is unreachable!");
        }
        if(realLength > ship.getMovementRange()) {
            throw new WrongMoveException("Your move is too long!");
        }
    }

    private int taxiLength(Integer x1, Integer y1, Integer x2, Integer y2) {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }

    private AttackResult attack(MoveRequest request, Long battleId, Ship ship) {
        Position position = positionRepository.findByXAndYAndBattleId(request.getX(), request.getY(), battleId).orElseThrow(WrongPositionException::new);
        if(position.getShip() == null) {
            throw new WrongMoveException("Nothing to attack!");
        }
        if(taxiLength(ship.getPosition().getX(), ship.getPosition().getY(), request.getX(), request.getY()) > ship.getAttackRange()) {
            throw new WrongMoveException("Target is too far away!");
        }
        if(ship.isAction()) {
            throw new WrongMoveException("Ship already made an action!");
        }
        Ship attackedShip = position.getShip();
        int damage = applyDamage(ship, attackedShip);
        shipRepository.save(attackedShip);
        shipRepository.updateActionById(ship.getId());
        AttackResult result = new AttackResult();
        result.setShipId(ship.getShipId());
        result.setX(request.getX());
        result.setY(request.getY());
        result.setDamage(damage);
        return result;
    }

    private void use(MoveRequest request, Long battleId, Ship ship) {
        // TODO let player use skills/items
    }

    private void testShipMove(Battle battle) {
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
        Optional<Position> newPosition = testShipPlacement(request, battleId, battle);
        Ship ship = saveShip(request, userId, battle);
        savePosition(request, ship, newPosition);
        return prepareMoveResponse(MoveAction.PREPARE);
    }

    private Optional<Position> testShipPlacement(MoveRequest request, Long battleId, Battle battle) {
        if(battle.isStarted()) {
            throw new WrongBattleStateException("Preparation stage ended. You cannot place ships!");
        }
        return testNewPosition(request, battleId);
    }

    private Optional<Position> testNewPosition(MoveRequest request, Long battleId) {
        if(request.getY() == null || request.getX() == null) {
            throw new WrongPositionException("Position cannot be empty!");
        }
        Optional<Position> position = positionRepository.findByXAndYAndBattleId(request.getX(), request.getY(), battleId);
        if(position.isPresent() && (position.get().getShip() != null || (position.get().getTerrain() != null && position.get().getTerrain().isBlocked()))) {
            throw new WrongPositionException();
        }
        return position;
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

    private void savePosition(MoveRequest request, Ship ship, Optional<Position> newPosition) {
        TerrainType terrain = newPosition.map(Position::getTerrain).orElse(null);
        changeOldPosition(ship, newPosition);
        Position position = ship.getPosition() == null ? new Position() : ship.getPosition();
        position.setShip(ship);
        position.setX(request.getX());
        position.setY(request.getY());
        position.setTerrain(terrain);
        positionRepository.save(position);
    }

    private void changeOldPosition(Ship ship, Optional<Position> newPosition) {
        if(ship.getPosition() != null && ship.getPosition().getTerrain() != null) {
            Position oldPosition = newPosition.orElse(new Position());
            oldPosition.setX(ship.getPosition().getX());
            oldPosition.setY(ship.getPosition().getY());
            oldPosition.setTerrain(ship.getPosition().getTerrain());
            positionRepository.save(oldPosition);
        } else {
            newPosition.ifPresent(positionRepository::delete);
        }
    }

    private MoveResponse prepareMoveResponse(MoveAction action) {
        return prepareMoveResponse(action, null, null);
    }

    private MoveResponse prepareMoveResponse(MoveAction action, MoveResult move, AttackResult attack) {
        MoveResponse response = new MoveResponse();
        response.setAction(action);
        response.setSuccess(true);
        response.setMove(move);
        response.setAttack(attack);
        return response;
    }

    @Override
    public List<MoveResponse> endTurn(Long battleId, Long userId) {
        Battle battle = battleRepository.findByIdAndExpeditionUserId(battleId, userId).orElseThrow(BattleNotFoundException::new);
        if(battle.isFinished()) {
            throw new WrongBattleStateException("Battle is already finished!");
        }

        List<MoveResponse> moves = new ArrayList<>();

        if(battle.isStarted()) {
            List<Ship> ships = shipRepository.findByExpeditionId(battle.getExpedition().getId());
            List<Ship> playerShips = filterPlayerShips(ships).toList();
            List<Ship> enemyShips = filterEnemyShips(ships).toList();
            for(Ship ship : playerShips) {
                ship.setMovement(false);
                ship.setAction(false);
            }
            moves = makeEnemyMove(playerShips, enemyShips.stream().filter((s) -> !s.isDestroyed()).toList(), battle);
            shipRepository.saveAll(playerShips);
            if(evaluateObjective(battle, enemyShips)) {
                battle.setFinished(true);
            }
            battle.setTurn(battle.getTurn()+1);
        } else {
            List<Ship> ships = shipRepository.findByExpeditionIdAndEnemyFalse(battle.getExpedition().getId());
            if(allPrepared(ships)) {
                throw new WrongMoveException("Not all ships are placed!");
            }
            battle.setStarted(true);
            battle.setTurn(1);
        }
        battleRepository.save(battle);

        return moves;
    }

    private boolean evaluateObjective(Battle battle, List<Ship> enemyShips) {
        return resultEvaluators.stream()
                .filter((a) -> a.ofType(battle))
                .map((a) -> a.evaluate(battle, enemyShips))
                .filter((a) -> a)
                .findFirst().orElse(false);
    }

    private List<MoveResponse> makeEnemyMove(List<Ship> playerShips, List<Ship> enemyShips, Battle battle) {
        List<MoveResponse> moves = new ArrayList<>();
        List<Position> positions = positionRepository.findByBattleId(battle.getId());
        List<Position> positionsToDelete = new ArrayList<>();
        for(Ship ship : enemyShips) {
            EnemyMoveTarget target = chooseTarget(ship, playerShips, battle, positions);
            if(target != null) {
                if(target.getPosition() != null && positionsAreDifferent(ship, target)) {
                    Optional<Position> targetPosition = getPositionOfTarget(target, positions);
                    Optional<Position> oldShipPosition = getPositionOfShip(ship, positions);
                    moveTowards(ship, target, targetPosition.map(Position::getTerrain).orElse(null));
                    updatePositionsAfterMovement(positions, targetPosition, oldShipPosition);
                    if(targetPosition.isPresent() && targetPosition.get().getId() != null) {
                        positionsToDelete.add(targetPosition.get());
                    }
                    moves.add(responseForMove(ship));
                }
                int damage = applyDamage(ship, target.getTarget());
                updatePositionsIfDestroyed(target, positions);
                moves.add(responseForAttack(ship, target.getTarget(), damage));
            }
        }
        positionRepository.deleteAll(positionsToDelete);
        shipRepository.saveAll(enemyShips);
        return moves;
    }

    private void updatePositionsAfterMovement(List<Position> positions, Optional<Position> oldPosition, Optional<Position> shipPosition) {
        oldPosition.ifPresent(positions::remove);
        if(shipPosition.isPresent() && oldPosition.isPresent()) {
            Position newPosition = new Position();
            newPosition.setX(shipPosition.get().getX());
            newPosition.setY(shipPosition.get().getY());
            newPosition.setTerrain(shipPosition.get().getTerrain());
            positions.add(newPosition);
            shipPosition.get().setTerrain(oldPosition.get().getTerrain());
            shipPosition.get().setX(oldPosition.get().getX());
            shipPosition.get().setY(oldPosition.get().getY());
        }
    }

    private void updatePositionsIfDestroyed(EnemyMoveTarget target, List<Position> positions) {
        if(target.getTarget().isDestroyed()) {
            getPositionOfTarget(target, positions)
                    .ifPresent((a) -> a.getShip().setDestroyed(true));
        }
    }

    private boolean positionsAreDifferent(Ship ship, EnemyMoveTarget target) {
        return !Objects.equals(target.getPosition().getX(), ship.getPosition().getX()) || !Objects.equals(target.getPosition().getY(), ship.getPosition().getY());
    }

    private MoveResponse responseForMove(Ship ship) {
        MoveResponse response = new MoveResponse();
        response.setSuccess(true);
        response.setAction(MoveAction.MOVE);
        MoveResult result = new MoveResult();
        result.setX(ship.getPosition().getX());
        result.setY(ship.getPosition().getY());
        result.setShipId(ship.getId());
        response.setMove(result);
        return response;
    }

    private MoveResponse responseForAttack(Ship ship, Ship target, int damage) {
        MoveResponse response = new MoveResponse();
        response.setSuccess(damage > 0);
        response.setAction(MoveAction.ATTACK);
        AttackResult result = new AttackResult();
        result.setX(target.getPosition().getX());
        result.setY(target.getPosition().getY());
        result.setShipId(ship.getId());
        result.setDamage(damage);
        response.setAttack(result);
        return response;
    }

    private void moveTowards(Ship ship, EnemyMoveTarget target, TerrainType terrain) {
        if(target == null || target.getPosition() == null) {
            return;
        }
        Position position = ship.getPosition();
        position.setX(target.getPosition().getX());
        position.setY(target.getPosition().getY());
        position.setTerrain(terrain);
    }

    private Optional<Position> getPositionOfTarget(EnemyMoveTarget target, List<Position> positions) {
        return positions.stream()
                .filter((a) -> Objects.equals(target.getPosition().getX(), a.getX()) && Objects.equals(target.getPosition().getY(), a.getY()))
                .findFirst();
    }

    private Optional<Position> getPositionOfShip(Ship ship, List<Position> positions) {
        return positions.stream()
                .filter((a) -> Objects.equals(ship.getPosition().getX(), a.getX()) && Objects.equals(ship.getPosition().getY(), a.getY()))
                .findFirst();
    }

    private int applyDamage(Ship ship, Ship target) {
        Random random = new Random();
        int hit = random.nextInt(100);
        if(hit < ship.getHitRate()) {
            int critical = random.nextInt(100);
            int damage = critical < ship.getCriticalRate() ? ship.getStrength()*CRITICAL_MULTI : ship.getStrength();
            target.setDamaged(true);
            target.setHp(target.getHp() - damage);
            if (target.getHp() <= 0) {
                target.setDestroyed(true);
                target.setPosition(null);
            }
            return damage;
        }
        return 0;
    }

    private EnemyMoveTarget chooseTarget(Ship ship, List<Ship> playerShips, Battle battle, List<Position> positions) {
        List<EnemyMoveTarget> targets = playerShips.stream()
                .filter((a) -> !a.isDestroyed())
                .map((a) -> getPotentialMove(ship, a, battle, positions))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        EnemyMoveTarget target = null;
        int maxDamage = 0;
        for(EnemyMoveTarget potentialTarget : targets) {
            int damage = calculateDamage(ship, potentialTarget.getTarget());
            boolean targetDies = damage < 0;
            if(targetDies) {
                return potentialTarget;
            } else if(damage > maxDamage) {
                target = potentialTarget;
            }
        }
        return target;
    }

    private Optional<EnemyMoveTarget> getPotentialMove(Ship ship, Ship target, Battle battle, List<Position> positions) {
        if(taxiLength(ship.getPosition().getX(), ship.getPosition().getY(), target.getPosition().getX(), target.getPosition().getY()) < ship.getAttackRange()) {
            return Optional.of(new EnemyMoveTarget(ship.getPosition(), target));
        }
        List<Position> positionsInRange = new ArrayList<>();
        for(int i=0; i<battle.getWidth(); i++) {
            for(int j=0; j<battle.getHeight(); j++) {
                Position position = new Position();
                position.setX(i);
                position.setY(j);
                if(taxiLength(target.getPosition().getX(), target.getPosition().getY(), position.getX(), position.getY()) == ship.getAttackRange()) {
                    positionsInRange.add(position);
                }
            }
        }
        return positionsInRange.stream()
                .filter((a) -> canBeReached(ship, battle, positions, a))
                .map((a) -> new EnemyMoveTarget(a, target))
                .findAny();
    }

    private boolean canBeReached(Ship ship, Battle battle, List<Position> positions, Position a) {
        int distance = distanceEvaluator.shortestPath(positions, ship.getPosition(), a, battle);
        return distance != -1 && distance < ship.getMovementRange();
    }

    private int calculateDamage(Ship ship, Ship target) {
        int damage = (int) ((int) (ship.getStrength() + CRITICAL_MULTI*ship.getStrength()*(ship.getCriticalRate()/100.0))*(ship.getHitRate()/100.0));
        if(target.getHp() < damage) {
            return -1;
        }
        return damage;
    }

    private boolean allPrepared(List<Ship> ships) {
        return ships.stream().anyMatch((s) -> !s.isPrepared());
    }
}
