package io.github.xpakx.habittracker.repo;

import io.github.xpakx.habittracker.entity.HabitContext;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitContextRepository extends JpaRepository<HabitContext, Long> {
}