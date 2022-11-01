package io.github.xpakx.habitcity.clients;

import io.github.xpakx.habitcity.clients.dto.ExpeditionEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExpeditionPublisher {
    private final AmqpTemplate template;
    private final String completionsTopic;

    public ExpeditionPublisher(AmqpTemplate template, @Value("${amqp.exchange.expeditions}") String expeditionsTopic) {
        this.template = template;
        this.completionsTopic = expeditionsTopic;
    }

    public void sendExpedition(Object expedition) {
        ExpeditionEvent event = new ExpeditionEvent();
        template.convertAndSend(completionsTopic, "expedition", event);
    }
}
