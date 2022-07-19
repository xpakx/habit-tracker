package io.github.xpakx.habittracker.repo;

import io.github.xpakx.habittracker.entity.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
}