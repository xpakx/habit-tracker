package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRepository extends JpaRepository<Habit, Long> {
}