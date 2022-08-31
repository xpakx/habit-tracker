package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.ContextDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitContextRepository extends JpaRepository<HabitContext, Long> {
    List<ContextDetails> findProjectedByUserId(Long userId);
    Optional<HabitContext> findByIdAndUserId(Long id, Long userId);
}