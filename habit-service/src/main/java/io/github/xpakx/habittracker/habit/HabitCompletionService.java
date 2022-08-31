package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.CompletionRequest;

public interface HabitCompletionService {
    HabitCompletion completeHabit(Long habitId, CompletionRequest request, Long userId);
}
