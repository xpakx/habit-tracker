package io.github.xpakx.habitgame.expedition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipRepository extends JpaRepository<Ship, Long> {
    List<Ship> findByExpeditionId(Long expeditionId);

    Optional<Ship> findByIdAndUserIdAndExpeditionId(Long id, Long userId, Long expeditionId);
    
}