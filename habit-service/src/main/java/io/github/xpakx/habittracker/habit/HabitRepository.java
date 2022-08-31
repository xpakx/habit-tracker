package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitDetails;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByNextDueBetweenAndUserId(LocalDateTime nextDueStart, LocalDateTime nextDueEnd, Long userId);
    List<Habit> findAllByUserId(Long userId);

    Optional<Habit> findByIdAndUserId(Long id, Long userId);

    @EntityGraph("habit-details")
    List<HabitDetails> findByNextDueBetweenAndContextId(LocalDateTime nextDueStart, LocalDateTime nextDueEnd, Long contextId);

    List<Habit> findByContextId(Long contextId);
}