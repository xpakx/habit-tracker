package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitDetails;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByNextDueBetween(LocalDateTime nextDueStart, LocalDateTime nextDueEnd);

    @EntityGraph("habit-details")
    List<HabitDetails> findByNextDueBetweenAndContextId(LocalDateTime nextDueStart, LocalDateTime nextDueEnd, Long contextId);

    List<Habit> findByContextId(Long contextId);
}