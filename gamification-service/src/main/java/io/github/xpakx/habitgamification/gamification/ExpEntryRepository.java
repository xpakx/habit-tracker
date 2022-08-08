package io.github.xpakx.habitgamification.gamification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExpEntryRepository extends JpaRepository<ExpEntry, Long> {
    @Query("SELECT SUM(e.experience) FROM ExpEntry e WHERE e.userId = :userId")
    Integer getExpForUser(Long userId);
}