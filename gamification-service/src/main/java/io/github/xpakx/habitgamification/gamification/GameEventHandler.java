package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameEventHandler {
    private final GamificationService gamificationService;

    @RabbitListener(queues = "${amqp.queue.gamification}")
    void handleHabitCompletion(final HabitCompletionEvent event) {
        try {
            gamificationService.newAttempt(event);
        } catch (final Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
