package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    long countByDateBetweenAndHabitId(LocalDateTime dateStart, LocalDateTime dateEnd, Long habitId);

}