package io.github.xpakx.habittracker.habit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    long countByDateBetweenAndHabitId(LocalDateTime dateStart, LocalDateTime dateEnd, Long habitId);
    List<HabitCompletion> findByUserIdAndDateAfter(Long userId, LocalDateTime date);
    List<HabitCompletion> findByUserIdAndHabitIdAndDateAfter(Long userId, Long habitId, LocalDateTime date);
    List<HabitCompletion> findByUserIdAndHabitContextIdAndDateAfter(Long userId, Long contextId, LocalDateTime date);
}
