package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.ExpeditionSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpeditionRepository extends JpaRepository<Expedition, Long> {
    List<ExpeditionSummary> findByUserIdAndFinishedIsFalse(Long userId);
}