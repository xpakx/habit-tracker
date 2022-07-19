package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
}