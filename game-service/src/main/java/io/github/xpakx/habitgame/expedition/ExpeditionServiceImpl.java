package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.*;
import io.github.xpakx.habitgame.expedition.error.ExpeditionHasResultException;
import io.github.xpakx.habitgame.expedition.error.ExpeditionNotFinishedException;
import io.github.xpakx.habitgame.expedition.error.ExpeditionNotFoundException;
import io.github.xpakx.habitgame.expedition.error.ExpeditionNotReturnedException;
import io.github.xpakx.habitgame.island.Island;
import io.github.xpakx.habitgame.island.IslandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpeditionServiceImpl implements ExpeditionService {
    private final ExpeditionRepository expeditionRepository;
    private final ItemRepository itemRepository;
    private final ShipRepository shipRepository;
    private final IslandRepository islandRepository;
    private final ExpeditionResultRepository resultRepository;
    private final ReturningExpeditionPublisher publisher;

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
        ship.setDamaged(false);
        ship.setDestroyed(false);
        return ship;
    }

    private Island getDefaultIsland() {
        return null;
    }

    @Override
    public ExpeditionResultResponse getResult(Long expeditionId, Long userId) {
        Expedition expedition = expeditionRepository.findById(expeditionId).orElseThrow(ExpeditionNotFoundException::new);
        testIfExpeditionIsFinished(expedition);
        testIfExpeditionHasResult(expedition);
        ExpeditionResult result = new ExpeditionResult();
        result.setExpedition(expedition);
        result.setType(generateResult(expedition.getDestination() != null));
        result.setCompleted(result.getType() == ResultType.NONE);
        resultRepository.save(result);
        return getResultResponse(result);
    }

    private void testIfExpeditionHasResult(Expedition expedition) {
        if(expedition.getExpeditionResult() != null) {
            throw new ExpeditionHasResultException();
        }
    }

    private ExpeditionResultResponse getResultResponse(ExpeditionResult result) {
        ExpeditionResultResponse response = new ExpeditionResultResponse();
        response.setResult(result.getType().getName());
        return response;
    }

    private ResultType generateResult(boolean islandAsDestination) {
        Random random = new Random();
        int rand = random.nextInt(10);
        if(rand == 0 && !islandAsDestination) {
            return ResultType.ISLAND;
        } else if(rand < 5) {
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
        if(expedition.getEnd().isAfter(LocalDateTime.now())) {
            throw new ExpeditionNotFinishedException();
        }
    }

    private void testIfExpeditionReturned(Expedition expedition) {
        if(expedition.getReturnEnd().isAfter(LocalDateTime.now())) {
            throw new ExpeditionNotReturnedException();
        }
    }

    @Override
    public ActionResponse completeExpedition(ActionRequest request, Long expeditionId, Long userId) {
        if(!request.isAction()) {
            return getActionResponse(false, expeditionId);
        }
        Expedition expedition = expeditionRepository.findById(expeditionId).orElseThrow(ExpeditionNotFoundException::new);
        if(expedition.getExpeditionResult() == null) {
            throw new ExpeditionNotFinishedException();
        }
        if(!expedition.getExpeditionResult().isCompleted()) {
            throw new ExpeditionNotFinishedException("Task not completed!");
        }
        expedition.setReturning(true);
        expedition.setReturnEnd(LocalDateTime.now().plusHours(10));
        expeditionRepository.save(expedition);

        return getActionResponse(true, expeditionId);
    }

    @Override
    public ActionResponse returnToCity(ActionRequest request, Long expeditionId, Long userId) {
        if(!request.isAction()) {
            return getActionResponse(false, expeditionId);
        }
        Expedition expedition = expeditionRepository.findById(expeditionId).orElseThrow(ExpeditionNotFoundException::new);
        testIfExpeditionReturning(expedition);
        testIfExpeditionReturned(expedition);
        expedition.setFinished(true);
        expeditionRepository.save(expedition);

        publisher.sendExpedition(shipRepository.findByExpeditionId(expeditionId), null, extractTreasure(expedition), userId);

        return getActionResponse(true, expeditionId);
    }

    private Treasure extractTreasure(Expedition expedition) {
        return expedition.getExpeditionResult() != null ? expedition.getExpeditionResult().getTreasure() : null;
    }

    private ActionResponse getActionResponse(boolean completed, Long expeditionId) {
        ActionResponse response = new ActionResponse();
        response.setCompleted(completed);
        response.setExpeditionId(expeditionId);
        return response;
    }

    private void testIfExpeditionReturning(Expedition expedition) {
        if(!expedition.isReturning()) {
            throw new ExpeditionNotReturnedException();
        }
    }

    @Override
    public boolean completeResult(Long expeditionId) {
        Optional<ExpeditionResult> result = resultRepository.findByExpeditionId(expeditionId);
        if(result.isEmpty() || !result.get().isCompleted()) {
            return false;
        }
        result.get().setCompleted(true);
        resultRepository.save(result.get());
        return true;
    }
}
