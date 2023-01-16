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
public class DefaultBattleGenerator extends AbstractBattleGenerator {
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
        return generateShips(expedition, random, rarities, prototypes);
    }

    protected List<ShipType> getRandomTypes(Integer rarity) {
        return shipTypeRepository.findRandomTypes(1, rarity);
    }

    protected Battle getReferenceToBattle(Long battleId) {
        return battleRepository.getReferenceById(battleId);
    }
}
