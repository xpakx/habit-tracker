package io.github.xpakx.habittracker.repo;

import io.github.xpakx.habittracker.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRepository extends JpaRepository<Habit, Long> {
}