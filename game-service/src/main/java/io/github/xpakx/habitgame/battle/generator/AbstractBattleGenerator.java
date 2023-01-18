package io.github.xpakx.habitgame.battle.generator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.Position;
import io.github.xpakx.habitgame.expedition.Expedition;
import io.github.xpakx.habitgame.expedition.ExpeditionResult;
import io.github.xpakx.habitgame.expedition.Ship;
import io.github.xpakx.habitgame.ship.ShipType;

import java.util.*;

public abstract class AbstractBattleGenerator implements BattleGenerator {
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
        return battle;
    }

    @Override
    public List<Ship> generateShips(Long battleId, Expedition expedition, Random random) {
        List<Integer> rarities = getRarities(expedition);
        List<ShipType> prototypes = getShipTypes(rarities);
        return generateShips(expedition, random, rarities, prototypes);
    }

    protected List<Integer> getRarities(Expedition expedition) {
        return new ArrayList<>();
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
            position.setBattle(getReferenceToBattle(battleId));
            position.setShip(ship);
            result.add(position);
        }
        return result;
    }

    protected Battle getReferenceToBattle(Long battleId) {
        return null;
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
        long shipBonus = rarityCount > 1 ? random.nextLong((long) (0.2*rarityCount)) - (long) (0.1*rarityCount) : 0;
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

    protected List<ShipType> getRandomTypes(Integer rarity) {
        return new ArrayList<>();
    }
}
