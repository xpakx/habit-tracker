package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.ContextDetails;
import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import io.github.xpakx.habittracker.habit.dto.HabitDetails;

import java.time.LocalDate;
import java.util.List;

public interface HabitContextService {
    HabitContext addContext(HabitContextRequest request, Long userId);
    HabitContext updateContext(Long contextId, HabitContextRequest request, Long userId);
    List<HabitDetails> getHabitsForDayAndContext(LocalDate date, Long contextId, Long userId);
    List<Habit> getHabitsForContext(Long contextId, Long userId);

    List<ContextDetails> getAllContexts(Long userId);
}
