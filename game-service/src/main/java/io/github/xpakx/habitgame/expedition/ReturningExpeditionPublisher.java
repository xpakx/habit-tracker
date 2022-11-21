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
        template.convertAndSend(returningTopic, "expedition", event);
    }
}
