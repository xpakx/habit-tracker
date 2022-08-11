package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.HabitCompletion;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameEventHandler {
    private final GamificationService gamificationService;

    @RabbitListener(queues = "${amqp.queue.completions}")
    void handleMultiplicationSolved(final HabitCompletion event) {
        try {
            gamificationService.newAttempt(event);
        } catch (final Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
