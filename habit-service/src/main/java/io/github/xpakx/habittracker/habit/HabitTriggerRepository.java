package io.github.xpakx.habittracker.habit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HabitTriggerRepository extends JpaRepository<HabitTrigger, Long> {
    Optional<HabitTrigger> findByHabitId(Long id);
}
