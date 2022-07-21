package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.DayRequest;
import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;

import java.util.List;

public interface HabitContextService {
    HabitContext addContext(HabitContextRequest request);
    HabitContext updateContext(Long contextId, HabitContextRequest request);
    List<Habit> getHabitsForDayAndContext(DayRequest request, Long contextId);
    List<Habit> getHabitsForContext(Long contextId);
}
