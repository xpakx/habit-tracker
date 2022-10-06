package io.github.xpakx.habitgamification.badge;

import io.github.xpakx.habitgamification.badge.AchievementProcessor;
import io.github.xpakx.habitgamification.badge.Badge;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SilverAchievementProcessorImpl implements AchievementProcessor {
    @Override
    public Optional<Badge> process(HabitCompletionEvent completion, int experience) {
        if(experience >= 5000) {
            return Optional.of(type());
        }
        return Optional.empty();
    }

    @Override
    public Badge type() {
        return Badge.SILVER;
    }
}
