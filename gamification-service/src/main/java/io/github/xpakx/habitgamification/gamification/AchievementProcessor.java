package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.HabitCompletion;

import java.util.Optional;

public interface AchievementProcessor {
    Optional<Badge> process(HabitCompletion completion, int experience);
    Badge type();
}
