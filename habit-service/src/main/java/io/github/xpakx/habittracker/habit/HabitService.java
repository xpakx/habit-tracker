package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.Habit;
import io.github.xpakx.habittracker.habit.dto.DayRequest;
import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import io.github.xpakx.habittracker.habit.dto.HabitUpdateRequest;

import java.util.List;

public interface HabitService {
    Habit addHabit(HabitRequest request);
    Habit updateHabit(Long habitId, HabitUpdateRequest request);
    List<Habit> getHabitsForDay(DayRequest request);
    List<Habit> getHabitsForDayAndContext(DayRequest request, Long contextId);
}
