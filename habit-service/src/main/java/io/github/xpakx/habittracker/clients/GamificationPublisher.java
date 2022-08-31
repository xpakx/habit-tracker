package io.github.xpakx.habittracker.clients;

import io.github.xpakx.habittracker.clients.dto.CompletionEvent;
import io.github.xpakx.habittracker.habit.HabitCompletion;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GamificationPublisher {
    private final AmqpTemplate template;
    private final String completionsTopic;

    public GamificationPublisher(AmqpTemplate template, @Value("${amqp.exchange.completions}") String completionsTopic) {
        this.template = template;
        this.completionsTopic = completionsTopic;
    }

    public void sendCompletion(HabitCompletion completion) {
        CompletionEvent event = new CompletionEvent();
        event.setCompletionId(completion.getId());
        event.setHabitId(completion.getHabit().getId());
        event.setUserId(completion.getUserId());
        event.setDifficulty(completion.getHabit().getDifficulty());
        template.convertAndSend(completionsTopic, "completion", event);
    }
}
