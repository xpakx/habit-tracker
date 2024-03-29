package io.github.xpakx.habitgame.battle.generator;

import io.github.xpakx.habitgame.battle.*;
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
public class DefaultBattleGenerator implements BattleGenerator{
    protected final ShipRepository shipRepository;
    protected final ShipTypeRepository shipTypeRepository;
    protected final TerrainTypeRepository terrainRepository;

    @Override
    public Battle createBattle(ExpeditionResult result) {
        Battle battle = new Battle();
        battle.setExpedition(result.getExpedition());
        battle.setFinished(false);
        battle.setStarted(false);
        battle.setHeight(15);
        battle.setWidth(20);
        battle.setTurn(0);
        battle.setTurnsToSurvive(0);
        battle.setObjective(BattleObjective.DEFEAT);
        return battle;
    }

    protected List<ShipType> getRandomTypes(Integer rarity) {
        return shipTypeRepository.findRandomTypes(1, rarity);
    }

    protected List<Integer> getRarities(Expedition expedition) {
        return shipRepository.findByExpeditionId(expedition.getId()).stream()
                .map(Ship::getRarity)
                .toList();
    }

    @Override
    public List<Ship> generateShips(Long battleId, Expedition expedition, Random random) {
        List<Integer> rarities = getRarities(expedition);
        List<ShipType> prototypes = getShipTypes(rarities);
        return generateShips(expedition, random, rarities, prototypes);
    }

    @Override
    public List<Position> randomizePositions(List<Ship> ships, Battle battle, Random random) {
        List<Position> positions = generateAllPositionsInBattle(battle);
        List<Position> result = randomizeShipPosition(battle, ships, positions, random);
        randomizeTerrain(positions, result, battle, random);
        return result;
    }

    private List<Position> generateAllPositionsInBattle(Battle battle) {
        int boardWidth = battle.getWidth();
        int boardHeight = battle.getHeight();
        List<Position> positions = new ArrayList<>();
        for(int i = 0; i < boardWidth; i++) {
            for(int j = 0; j < boardHeight; j++) {
                Position pos = new Position();
                pos.setX(i);
                pos.setY(j);
                pos.setBattle(battle);
                positions.add(pos);
            }
        }
        return positions;
    }

    private List<Position> randomizeShipPosition(Battle battle, List<Ship> ships, List<Position> positions, Random random) {
        Collections.shuffle(positions, random);
        List<Position> positionsForEnemyShips = positions.stream().filter((a) -> a.getX() < battle.getWidth() / 2).toList();
        List<Position> result = new ArrayList<>();
        int positionIndex = 0;
        for(Ship ship : ships) {
            Position position = positionsForEnemyShips.get(positionIndex++);
            position.setShip(ship);
            result.add(position);
        }
        return result;
    }

    private void randomizeTerrain(List<Position> positions, List<Position> positionsToAdd, Battle battle, Random random) {
        List<TerrainType> terrainTypes = terrainRepository.findBySeizableFalse();
        if(terrainTypes.size() <= 0) {
            return;
        }
        Collections.shuffle(positions, random);
        int elementCount = random.nextInt((int) (battle.getWidth() * battle.getHeight()*0.1)+1);
        elementCount = elementCount >= positions.size() ? positions.size()-1 : elementCount;
        for(int i = 0; i<elementCount; i++) {
            TerrainType type = terrainTypes.get(random.nextInt(terrainTypes.size()));
            Position position = positions.get(i);
            if(!type.isBlocked() || position.getShip() != null) {
                position.setTerrain(type);
            }
            if(position.getShip() == null) {
                positionsToAdd.add(position);
            }
        }
    }

    protected Ship generateShipFromPrototype(Expedition expedition, ShipType prototype, Integer sizeBonus) {
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
        ship.setMovementRange(prototype.getMovementRange());
        ship.setAttackRange(prototype.getAttackRange());
        return ship;
    }

    protected List<Ship> generateShips(Expedition expedition, Random random, List<Integer> rarities, List<ShipType> shipPrototypes) {
        List<Ship> shipsToAdd = new ArrayList<>();
        for(ShipType prototype : shipPrototypes) {
            long ships = calculateShipCount(random, rarities, prototype);
            for(long i = ships; i>0; i--) {
                shipsToAdd.add(generateShipFromPrototype(expedition, prototype, random.nextInt(2)-1));
            }
        }
        return shipsToAdd;
    }

    protected long calculateShipCount(Random random, List<Integer> rarities, ShipType prototype) {
        long rarityCount = rarities.stream().filter((a) -> Objects.equals(a, prototype.getRarity())).count();
        long randomBound = (long) (0.2*rarityCount);
        long shipBonus = randomBound > 0 ? random.nextLong(randomBound) - (long) (0.1*rarityCount) : 0;
        return rarityCount + shipBonus;
    }

    protected List<ShipType> getShipTypes(List<Integer> rarities) {
        List<Integer> distinctRarities = rarities.stream().distinct().toList();
        List<ShipType> shipPrototypes = new ArrayList<>();
        for(Integer rarity : distinctRarities) {
            shipPrototypes.addAll(getRandomTypes(rarity));
        }
        return shipPrototypes;
    }
}
