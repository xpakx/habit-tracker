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
public class BossBattleGenerator extends AbstractBattleGenerator {
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
        battle.setObjective(BattleObjective.BOSS);
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

    protected List<ShipType> getRandomTypes(Integer rarity) {
        return shipTypeRepository.findRandomTypes(1, rarity);
    }

    protected Battle getReferenceToBattle(Long battleId) {
        return battleRepository.getReferenceById(battleId);
    }
}
