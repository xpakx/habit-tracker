package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.CompletionResult;
import io.github.xpakx.habitgamification.gamification.dto.ExpResponse;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;

public interface GamificationService {
    CompletionResult newAttempt(HabitCompletionEvent completion);
    ExpResponse getExp(Long userId);
}
