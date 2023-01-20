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
    private final ShipRepository shipRepository;
    private final ShipTypeRepository shipTypeRepository;

    @Override
    public Battle createBattle(ExpeditionResult result) {
        Battle battle = super.createBattle(result);
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
}
