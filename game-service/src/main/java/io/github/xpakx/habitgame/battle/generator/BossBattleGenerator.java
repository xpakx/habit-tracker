package io.github.xpakx.habitgame.battle.generator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.BattleObjective;
import io.github.xpakx.habitgame.battle.TerrainTypeRepository;
import io.github.xpakx.habitgame.expedition.Expedition;
import io.github.xpakx.habitgame.expedition.ExpeditionResult;
import io.github.xpakx.habitgame.expedition.Ship;
import io.github.xpakx.habitgame.expedition.ShipRepository;
import io.github.xpakx.habitgame.ship.ShipType;
import io.github.xpakx.habitgame.ship.ShipTypeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BossBattleGenerator extends DefaultBattleGenerator {

    public BossBattleGenerator(ShipRepository shipRepository, ShipTypeRepository shipTypeRepository, TerrainTypeRepository terrainRepository) {
        super(shipRepository, shipTypeRepository, terrainRepository);
    }

    @Override
    public Battle createBattle(ExpeditionResult result) {
        Battle battle = super.createBattle(result);
        battle.setObjective(BattleObjective.BOSS);
        return battle;
    }

    @Override
    public List<Ship> generateShips(Long battleId, Expedition expedition, Random random) {
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
}
