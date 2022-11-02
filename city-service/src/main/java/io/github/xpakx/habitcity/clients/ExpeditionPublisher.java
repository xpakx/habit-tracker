package io.github.xpakx.habitcity.clients;

import io.github.xpakx.habitcity.clients.dto.EventShip;
import io.github.xpakx.habitcity.clients.dto.ExpeditionEvent;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.ship.PlayerShip;
import io.github.xpakx.habitcity.ship.Ship;
import io.github.xpakx.habitcity.ship.dto.ExpeditionEquipment;
import io.github.xpakx.habitcity.ship.dto.ExpeditionRequest;
import io.github.xpakx.habitcity.ship.dto.ExpeditionShip;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpeditionPublisher {
    private final AmqpTemplate template;
    private final String completionsTopic;

    public ExpeditionPublisher(AmqpTemplate template, @Value("${amqp.exchange.expeditions}") String expeditionsTopic) {
        this.template = template;
        this.completionsTopic = expeditionsTopic;
    }

    public void sendExpedition(ExpeditionRequest request, List<PlayerShip> ships, List<Resource> resources, Long userId) {
        Map<Long, Ship> shipMap = ships.stream()
                .collect(Collectors.toMap(PlayerShip::getId, PlayerShip::getShip));
        Map<Long, Resource> resourceMap = resources.stream()
                .collect(Collectors.toMap(Resource::getId, (a) -> a));
        ExpeditionEvent event = new ExpeditionEvent();
        event.setIslandId(request.getIslandId());
        event.setUserId(userId);
        event.setShips(prepareShips(request, shipMap, resourceMap));
        template.convertAndSend(completionsTopic, "expedition", event);
    }

    private List<EventShip> prepareShips(ExpeditionRequest request, Map<Long, Ship> shipMap, Map<Long, Resource> resourceMap) {
        return request.getShips().stream().map((a) -> createEventShip(shipMap, resourceMap, a)).toList();
    }

    private EventShip createEventShip(final Map<Long, Ship> shipMap,final Map<Long, Resource> resourceMap, ExpeditionShip ship) {
        EventShip shipForEvent = new EventShip();
        Ship shipFromDb = shipMap.getOrDefault(ship.getShipId(), null);
        shipForEvent.setShipId(ship.getShipId());
        shipForEvent.setCode(shipFromDb.getCode());
        shipForEvent.setName(shipFromDb.getName());
        shipForEvent.setMaxCargo(shipFromDb.getMaxCargo());
        shipForEvent.setRarity(shipFromDb.getRarity());
        shipForEvent.setSize(shipFromDb.getSize());
        return shipForEvent;
    }
}
