package io.github.xpakx.habitgame.battle.generator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.BattleObjective;
import io.github.xpakx.habitgame.battle.BattleRepository;
import io.github.xpakx.habitgame.battle.Position;
import io.github.xpakx.habitgame.expedition.Expedition;
import io.github.xpakx.habitgame.expedition.ExpeditionResult;
import io.github.xpakx.habitgame.expedition.Ship;
import io.github.xpakx.habitgame.expedition.ShipRepository;
import io.github.xpakx.habitgame.ship.ShipType;
import io.github.xpakx.habitgame.ship.ShipTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class BossBattleGenerator implements BattleGenerator {
    private final BattleRepository battleRepository;
    private final ShipRepository shipRepository;
    private final ShipTypeRepository shipTypeRepository;

    @Override
    public Battle createBattle(ExpeditionResult result) {
        Battle battle = new Battle();
        battle.setExpedition(result.getExpedition());
        battle.setFinished(false);
        battle.setStarted(false);
        battle.setHeight(15);
        battle.setWidth(20);
        battle.setObjective(BattleObjective.DEFEAT);
        battle.setTurn(0);
        battle.setTurnsToSurvive(0);
        return battle;
    }

    @Override
    public List<Ship> generateShips(Long battleId, Expedition expedition) {
        Random random = new Random();
        List<Integer> rarities = shipRepository.findByExpeditionId(expedition.getId()).stream()
                .map(Ship::getRarity)
                .toList();
        List<ShipType> prototypes = getShipTypes(rarities);
        List<Ship> ships =  generateShips(expedition, random, rarities, prototypes);
        generateBossShip(expedition, prototypes)
                .ifPresent(ships::add);
        return ships;
    }

    private Optional<Ship> generateBossShip(Expedition expedition, List<ShipType> prototypes) {
        int maxRarity = prototypes.stream().map(ShipType::getRarity).max(Comparator.naturalOrder()).orElse(0);
        return prototypes.stream()
                .filter(a -> a.getRarity() == maxRarity).map((a) -> generateBossFromPrototype(a, expedition))
                .findFirst();
    }

    private Ship generateBossFromPrototype(ShipType prototype, Expedition expedition) {
        Random random = new Random();
        Ship ship = generateShipFromPrototype(expedition, prototype, random.nextInt(5)-1);
        ship.setBoss(true);
        return ship;
    }

    @Override
    public List<Position> randomizePositions(List<Ship> ships, Long battleId, Random random) {
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
        for(ShipType prototype : shipPrototypes) {
            long ships = calculateShipCount(random, rarities, prototype);
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
        ship.setHp(ship.getSize()*10);
        ship.setStrength(prototype.getStrength());
        ship.setCriticalRate(prototype.getCriticalRate());
        ship.setHitRate(prototype.getHitRate());
        return ship;
    }
}
