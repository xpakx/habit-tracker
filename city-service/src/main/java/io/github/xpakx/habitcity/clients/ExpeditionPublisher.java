package io.github.xpakx.habitcity.clients;

import io.github.xpakx.habitcity.clients.dto.ExpeditionEvent;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.ship.PlayerShip;
import io.github.xpakx.habitcity.ship.Ship;
import io.github.xpakx.habitcity.ship.dto.ExpeditionRequest;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpeditionPublisher {
    private final AmqpTemplate template;
    private final String completionsTopic;

    public ExpeditionPublisher(AmqpTemplate template, @Value("${amqp.exchange.expeditions}") String expeditionsTopic) {
        this.template = template;
        this.completionsTopic = expeditionsTopic;
    }

    public void sendExpedition(ExpeditionRequest request, List<PlayerShip> ships, List<Resource> resources, Long userId) {
        ExpeditionEvent event = new ExpeditionEvent();
        event.setIslandId(1L);
        event.setUserId(userId);
        template.convertAndSend(completionsTopic, "expedition", event);
    }
}
