package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.HabitContext;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitContextRepository extends JpaRepository<HabitContext, Long> {
}