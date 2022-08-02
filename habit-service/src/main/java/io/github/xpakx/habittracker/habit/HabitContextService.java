package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.ContextDetails;
import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import io.github.xpakx.habittracker.habit.dto.HabitDetails;

import java.time.LocalDateTime;
import java.util.List;

public interface HabitContextService {
    HabitContext addContext(HabitContextRequest request);
    HabitContext updateContext(Long contextId, HabitContextRequest request);
    List<HabitDetails> getHabitsForDayAndContext(LocalDateTime request, Long contextId);
    List<Habit> getHabitsForContext(Long contextId);

    List<ContextDetails> getAllContexts();
}
