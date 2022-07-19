package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.Habit;
import io.github.xpakx.habittracker.habit.dto.HabitRequest;

public interface HabitService {
    Habit addHabit(HabitRequest request);
}
