package io.github.xpakx.habitgamification.badge;

import io.github.xpakx.habitgamification.badge.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}