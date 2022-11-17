package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.Cargo;
import io.github.xpakx.habitgame.expedition.dto.EventShip;
import io.github.xpakx.habitgame.expedition.dto.ExpeditionEvent;
import io.github.xpakx.habitgame.expedition.dto.ExpeditionResultResponse;
import io.github.xpakx.habitgame.island.Island;
import io.github.xpakx.habitgame.island.IslandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpeditionServiceImpl implements ExpeditionService {
    private final ExpeditionRepository expeditionRepository;
    private final ItemRepository itemRepository;
    private final ShipRepository shipRepository;
    private final IslandRepository islandRepository;

    @Override
    public void addExpedition(ExpeditionEvent event) {
        Island island = islandRepository.findById(event.getIslandId()).orElse(getDefaultIsland());
        Expedition expedition = new Expedition();
        expedition.setUserId(event.getUserId());
        expedition.setStart(LocalDateTime.now());
        expedition.setStart(LocalDateTime.now().plusHours(10));
        expedition.setDestination(island);
        expedition.setFinished(false);
        expeditionRepository.save(expedition);
        List<Ship> ships = event.getShips().stream().map(a -> toShip(a, expedition)).toList();
        Map<Long, Ship> shipMap = shipRepository.saveAll(ships).stream()
                .collect(Collectors.toMap(Ship::getShipId, a -> a));
        List<Item> items = new ArrayList<>();
        for(EventShip ship : event.getShips()) {
            items.addAll(ship.getCargo().stream().map(a -> toItem(a, shipMap.get(ship.getShipId()))).toList());
        }
        itemRepository.saveAll(items);
    }

    @Override
    public List<Expedition> getActiveExpeditions(Long userId) {
        return expeditionRepository.findByUserIdAndFinishedIsFalse(userId);
    }

    private Item toItem(Cargo cargo, Ship ship) {
        Item item = new Item();
        item.setCode(cargo.getCode());
        item.setName(cargo.getName());
        item.setAmount(cargo.getAmount());
        item.setRarity(cargo.getRarity());
        item.setResourceId(cargo.getResourceId());
        item.setShip(ship);
        return item;
    }

    private Ship toShip(EventShip eventShip, Expedition expedition) {
        Ship ship = new Ship();
        ship.setShipId(eventShip.getShipId());
        ship.setCode(eventShip.getCode());
        ship.setMaxCargo(eventShip.getMaxCargo());
        ship.setName(eventShip.getName());
        ship.setSize(eventShip.getSize());
        ship.setExpedition(expedition);
        return ship;
    }

    private Island getDefaultIsland() {
        return null;
    }

    @Override
    public ExpeditionResultResponse getResult(Long expeditionId, Long userId) {
        return null;
    }
}
