package io.github.xpakx.habittracker.habit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    long countByDateBetweenAndHabitId(LocalDateTime dateStart, LocalDateTime dateEnd, Long habitId);
    List<HabitCompletion> findByUserId(Long userId);
    List<HabitCompletion> findByUserIdAndHabitId(Long userId, Long habitId);
    List<HabitCompletion> findByUserIdAndHabitContextId(Long userId, Long contextId);
}
