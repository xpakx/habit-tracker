package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.Cargo;
import io.github.xpakx.habitgame.expedition.dto.EventShip;
import io.github.xpakx.habitgame.expedition.dto.ExpeditionEvent;
import io.github.xpakx.habitgame.island.Island;
import io.github.xpakx.habitgame.island.IslandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpeditionServiceImpl implements ExpeditionService {
    private final ExpeditionRepository expeditionRepository;
    private final ItemRepository itemRepository;
    private final ShipRepository shipRepository;
    private final IslandRepository islandRepository;

    @Override
    public void addExpedition(ExpeditionEvent event) {
        Island island = islandRepository.findById(event.getIslandId()).orElse(null);
        Expedition expedition = new Expedition();
        expedition.setUserId(event.getUserId());
        expedition.setStart(LocalDateTime.now());
        expedition.setStart(LocalDateTime.now().plusHours(10));
        expedition.setDestination(island);
        expeditionRepository.save(expedition);
    }
}
