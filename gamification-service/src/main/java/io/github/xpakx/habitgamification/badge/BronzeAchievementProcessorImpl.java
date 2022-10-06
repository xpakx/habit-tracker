package io.github.xpakx.habitgamification.badge;

import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BronzeAchievementProcessorImpl implements AchievementProcessor {
    @Override
    public Optional<Badge> process(HabitCompletionEvent completion, int experience) {
        if(experience >= 1000) {
            return Optional.of(type());
        }
        return Optional.empty();
    }

    @Override
    public Badge type() {
        return Badge.BRONZE;
    }
}
