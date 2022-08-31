package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import io.github.xpakx.habittracker.habit.dto.HabitUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface HabitService {
    Habit addHabit(HabitRequest request, Long userId);
    Habit updateHabit(Long habitId, HabitUpdateRequest request, Long userId);
    List<Habit> getHabitsForDay(LocalDateTime date, Long userId);
    List<Habit> getAllHabits(Long userId);
}
