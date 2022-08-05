package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.CompletionResult;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletion;

public interface GamificationService {
    CompletionResult newAttempt(HabitCompletion completion);
}
