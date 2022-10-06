package io.github.xpakx.habitgamification.badge;

import io.github.xpakx.habitgamification.badge.AchievementProcessor;
import io.github.xpakx.habitgamification.badge.Badge;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;

import java.util.Optional;

public class GoldAchievementProcessorImpl implements AchievementProcessor {
    @Override
    public Optional<Badge> process(HabitCompletionEvent completion, int experience) {
        if(experience >= 15000) {
            return Optional.of(type());
        }
        return Optional.empty();
    }

    @Override
    public Badge type() {
        return Badge.GOLD;
    }
}
