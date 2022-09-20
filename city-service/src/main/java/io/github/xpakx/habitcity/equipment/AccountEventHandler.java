package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.city.CityService;
import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import io.github.xpakx.habitcity.shop.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountEventHandler {
    private final EquipmentService eqService;
    private final ShopService shopService;
    private final CityService cityService;


    @RabbitListener(queues = "${amqp.queue.accounts}")
    void handleNewAccount(final AccountEvent event) {
        try {
            eqService.addUserEquipment(event);
            shopService.addUserShop(event);
            cityService.addUserCity(event);
        } catch (final Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
