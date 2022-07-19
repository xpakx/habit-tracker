package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.HabitTrigger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitTriggerRepository extends JpaRepository<HabitTrigger, Long> {
}