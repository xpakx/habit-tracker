package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.ContextDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitContextRepository extends JpaRepository<HabitContext, Long> {
    List<ContextDetails> findProjectedBy();
}