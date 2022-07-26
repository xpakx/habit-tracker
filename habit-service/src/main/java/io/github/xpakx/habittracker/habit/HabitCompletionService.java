package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.DayRequest;

public interface HabitCompletionService {
    HabitCompletion completeHabit(Long habitId, DayRequest request);
}
