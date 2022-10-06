package io.github.xpakx.habitgamification.badge.processor;

import io.github.xpakx.habitgamification.badge.Badge;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;

import java.util.Optional;

public interface AchievementProcessor {
    Optional<Badge> process(HabitCompletionEvent completion, int experience);
    Badge type();
}
