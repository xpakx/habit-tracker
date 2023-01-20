package io.github.xpakx.habitgame.battle;

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
        List<Ship> ships = shipRepository.findByExpeditionId(battle.getExpedition().getId()); //TODO optimize
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
        testNewPosition(request, battle.getId());
        testMove(ship, request, battle);
        Position position = ship.getPosition();
        position.setX(request.getX());
        position.setY(request.getY());
        positionRepository.save(position);
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
        // List<Position> positions = positionRepository.findByBattleId(battleId);
        if(taxiLength(ship.getPosition().getX(), ship.getPosition().getY(), request.getX(), request.getY()) > 3) {
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
        if(taxiLength(ship.getPosition().getX(), ship.getPosition().getY(), request.getX(), request.getY()) > 3) {
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
            moves = makeEnemyMove(playerShips, enemyShips.stream().filter((s) -> !s.isDestroyed()).toList());
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

    private List<MoveResponse> makeEnemyMove(List<Ship> playerShips, List<Ship> enemyShips) {
        List<MoveResponse> moves = new ArrayList<>();
        for(Ship ship : enemyShips) {
            Ship target = chooseTarget(ship, playerShips);
            if(target != null) {
                moveTowards(ship, target);
                int damage = applyDamage(ship, target);
                moves.add(responseForMove(ship));
                moves.add(responseForAttack(ship, target, damage));
            }
        }
        return moves;
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

    private void moveTowards(Ship ship, Ship target) {
        // TODO add better movement
        Position position = ship.getPosition();
        position.setX((ship.getPosition().getX()+target.getPosition().getX())/2);
        position.setY((ship.getPosition().getY()+target.getPosition().getY())/2);
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

    private Ship chooseTarget(Ship ship, List<Ship> playerShips) {
        List<Ship> targets = playerShips.stream()
                .filter((a) -> isInRange(ship, a))
                .filter((a) -> !a.isDestroyed())
                .toList();
        Ship target = null;
        int maxDamage = 0;
        for(Ship potentialTarget : targets) {
            int damage = calculateDamage(ship, potentialTarget);
            boolean targetDies = damage < 0;
            if(targetDies) {
                return potentialTarget;
            } else if(damage > maxDamage) {
                target = potentialTarget;
            }
        }
        return target;
    }

    private boolean isInRange(Ship ship, Ship target) {
        return taxiLength(ship.getPosition().getX(), ship.getPosition().getY(), target.getPosition().getX(), target.getPosition().getY()) < 6;
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
