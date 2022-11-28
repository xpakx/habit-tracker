package io.github.xpakx.habitgame.expedition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpeditionResultRepository extends JpaRepository<ExpeditionResult, Long> {
    Optional<ExpeditionResult> findByExpeditionId(Long id);
}