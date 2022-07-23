package io.github.xpakx.habittracker.habit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByNextDueBetween(LocalDateTime nextDueStart, LocalDateTime nextDueEnd);

    List<Habit> findByNextDueBetweenAndContextId(LocalDateTime nextDueStart, LocalDateTime nextDueEnd, Long contextId);

    List<Habit> findByContextId(Long contextId);
}