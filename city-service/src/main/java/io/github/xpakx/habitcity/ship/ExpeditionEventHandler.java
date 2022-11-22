package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.ship.dto.ExpeditionEndEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpeditionEventHandler {
    private final ShipService service;

    @RabbitListener(queues = "${amqp.queue.returning}")
    void handleExpedition(final ExpeditionEndEvent event) {
        try {
            service.unlockShips(event);
        } catch (final Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
