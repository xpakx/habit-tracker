package io.github.xpakx.habittracker.habit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitContextRepository extends JpaRepository<HabitContext, Long> {
}