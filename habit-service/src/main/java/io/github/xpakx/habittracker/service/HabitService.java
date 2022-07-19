package io.github.xpakx.habittracker.service;

import io.github.xpakx.habittracker.entity.Habit;
import io.github.xpakx.habittracker.entity.dto.HabitRequest;

public interface HabitService {
    Habit addHabit(HabitRequest request);
}
