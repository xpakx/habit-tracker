package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountEventHandler {
    private final EquipmentService service;

    @RabbitListener(queues = "${amqp.queue.accounts}")
    void handleNewAccount(final AccountEvent event) {
        try {
            service.addUserEquipment(event);
        } catch (final Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
