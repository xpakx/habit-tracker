package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.Habit;
import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import io.github.xpakx.habittracker.habit.dto.HabitUpdateRequest;

public interface HabitService {
    Habit addHabit(HabitRequest request);
    Habit updateHabit(Long habitId, HabitUpdateRequest request);
}
