package io.github.xpakx.habittracker.repo;

import io.github.xpakx.habittracker.entity.HabitTrigger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitTriggerRepository extends JpaRepository<HabitTrigger, Long> {
}