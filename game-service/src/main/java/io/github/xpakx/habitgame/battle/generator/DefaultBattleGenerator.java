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
        int boardWidth = battle.getWidth();
        int boardHeight = battle.getHeight();
        List<Position> positions = new ArrayList<>();
        for(int i = 0; i < boardWidth/2; i++) {
            for(int j = 0; j < boardHeight; j++) {
                Position pos = new Position();
                pos.setX(i);
                pos.setY(j);
                pos.setBattle(battle);
                positions.add(pos);
            }
        }
        Collections.shuffle(positions);
        List<Position> result = new ArrayList<>();
        int positionIndex = 0;
        for(Ship ship : ships) {
            Position position = positions.get(positionIndex++);
            position.setShip(ship);
            result.add(position);
        }
        randomizeTerrain(positions, result, battle);
        return result;
    }

    private void randomizeTerrain(List<Position> positions, List<Position> positionsToAdd, Battle battle) {
        List<TerrainType> terrainTypes = terrainRepository.findAll();
        if(terrainTypes.size() <= 0) {
            return;
        }
        Random random = new Random();
        int elementCount = random.nextInt((int) (battle.getWidth() * battle.getHeight()*0.1)+1);
        elementCount = elementCount >= positions.size() ? positions.size()-1 : elementCount;
        for(int i = 0; i<elementCount; i++) {
            TerrainType type = terrainTypes.get(random.nextInt(terrainTypes.size()));
            Position position = positions.get(i);
            position.setTerrain(type);
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
