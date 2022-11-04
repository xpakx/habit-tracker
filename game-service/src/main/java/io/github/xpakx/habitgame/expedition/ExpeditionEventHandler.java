package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.ExpeditionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpeditionEventHandler {
    private final ExpeditionService service;

    @RabbitListener(queues = "${amqp.queue.expeditions}")
    void handleNewAccount(final ExpeditionEvent event) {
        try {
            service.addExpedition(event);
        } catch (final Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
