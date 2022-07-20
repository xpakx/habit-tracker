package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;

public interface HabitContextService {
    HabitContext addContext(HabitContextRequest request);
    HabitContext updateContext(Long contextId, HabitContextRequest request);
}
