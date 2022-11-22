package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.Cargo;
import io.github.xpakx.habitgame.expedition.dto.ExpeditionEndEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReturningExpeditionPublisher {
    private final AmqpTemplate template;
    private final String returningTopic;

    public ReturningExpeditionPublisher(AmqpTemplate template, @Value("${amqp.exchange.returning}") String expeditionsTopic) {
        this.template = template;
        this.returningTopic = expeditionsTopic;
    }

    public void sendExpedition(List<Ship> ships, List<Cargo> cargo, Long userId) {
        ExpeditionEndEvent event = new ExpeditionEndEvent();
        event.setUserId(userId);
        event.setShipsIds(ships.stream().filter(a -> !a.isDamaged() && !a.isDestroyed()).map(Ship::getShipId).toList());
        event.setDamagedShipsIds(ships.stream().filter(Ship::isDamaged).map(Ship::getShipId).toList());
        event.setDestroyedShipsIds(ships.stream().filter(Ship::isDestroyed).map(Ship::getShipId).toList());
        template.convertAndSend(returningTopic, "expedition", event);
    }
}
