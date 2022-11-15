package io.github.xpakx.habitgame.expedition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpeditionRepository extends JpaRepository<Expedition, Long> {
    List<Expedition> findByUserIdAndFinishedIsFalse(Long userId);
}