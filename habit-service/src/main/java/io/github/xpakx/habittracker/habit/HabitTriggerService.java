package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.TriggerUpdateRequest;

public interface HabitTriggerService {
    HabitTrigger updateTrigger(Long habitId, TriggerUpdateRequest request, Long userId);
}
