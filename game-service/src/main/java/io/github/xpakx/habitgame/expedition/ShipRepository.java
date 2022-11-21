package io.github.xpakx.habitgame.expedition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipRepository extends JpaRepository<Ship, Long> {
    List<Ship> findByExpeditionId(Long expeditionId);
}