package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.*;
import io.github.xpakx.habitgame.expedition.error.ExpeditionNotFinishedException;
import io.github.xpakx.habitgame.island.Island;
import io.github.xpakx.habitgame.island.IslandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpeditionServiceImpl implements ExpeditionService {
    private final ExpeditionRepository expeditionRepository;
    private final ItemRepository itemRepository;
    private final ShipRepository shipRepository;
    private final IslandRepository islandRepository;
    private final ExpeditionResultRepository resultRepository;

    @Override
    public void addExpedition(ExpeditionEvent event) {
        Island island = islandRepository.findById(event.getIslandId()).orElse(getDefaultIsland());
        Expedition expedition = new Expedition();
        expedition.setUserId(event.getUserId());
        expedition.setStart(LocalDateTime.now());
        expedition.setStart(LocalDateTime.now().plusHours(10));
        expedition.setDestination(island);
        expedition.setFinished(false);
        expedition.setReturning(false);
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
        Expedition expedition = expeditionRepository.findById(expeditionId).orElseThrow();
        testIfExpeditionIsFinished(expedition);
        testIfExpeditionHasResult(expedition);
        ExpeditionResult result = new ExpeditionResult();
        result.setExpedition(expedition);
        result.setType(generateResult());
        result.setCompleted(result.getType() == ResultType.NONE);
        resultRepository.save(result);
        return getResultResponse(result);
    }

    private void testIfExpeditionHasResult(Expedition expedition) {
        if(expedition.getExpeditionResult() != null) {
            throw new ExpeditionNotFinishedException();
        }
    }

    private ExpeditionResultResponse getResultResponse(ExpeditionResult result) {
        ExpeditionResultResponse response = new ExpeditionResultResponse();
        response.setResult(result.getType().getName());
        return response;
    }

    private ResultType generateResult() {
        Random random = new Random();
        int rand = random.nextInt(10);
        if(rand < 5) {
            return ResultType.NONE;
        } else if (rand < 8) {
            return ResultType.BATTLE;
        } else if (rand == 8) {
            return ResultType.TREASURE;
        } else {
            return ResultType.MONSTER;
        }
    }

    private void testIfExpeditionIsFinished(Expedition expedition) {
        if(expedition.getEnd().isBefore(LocalDateTime.now())) {
            throw new ExpeditionNotFinishedException();
        }
    }

    @Override
    public ActionResponse completeExpedition(ActionRequest request, Long expeditionId, Long userId) {
        if(!request.isAction()) {
            ActionResponse response = new ActionResponse();
            response.setCompleted(false);
            response.setExpeditionId(expeditionId);
            return response;
        }
        Expedition expedition = expeditionRepository.findById(expeditionId).orElseThrow();
        if(expedition.getExpeditionResult() == null) {
            throw new ExpeditionNotFinishedException();
        }
        if(!expedition.getExpeditionResult().isCompleted()) {
            throw new ExpeditionNotFinishedException("Task not completed!");
        }
        expedition.setReturning(true);
        expedition.setReturnEnd(LocalDateTime.now().plusHours(10));

        ActionResponse response = new ActionResponse();
        response.setCompleted(true);
        response.setExpeditionId(expeditionId);
        return response;
    }

    @Override
    public ActionResponse returnToCity(ActionRequest request, Long expeditionId, Long userId) {
        return null;
    }
}
