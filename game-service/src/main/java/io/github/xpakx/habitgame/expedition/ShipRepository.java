package io.github.xpakx.habitgame.expedition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ShipRepository extends JpaRepository<Ship, Long> {
    List<Ship> findByExpeditionId(Long expeditionId);
    List<Ship> findByExpeditionIdAndEnemyFalse(Long id);

    Optional<Ship> findByIdAndUserIdAndExpeditionId(Long id, Long userId, Long expeditionId);

    @Transactional
    @Modifying
    @Query("update Ship s set s.movement = true where s.id = ?1")
    int updateMovementById(Long id);

    @Transactional
    @Modifying
    @Query("update Ship s set s.action = true where s.id = ?1")
    int updateActionById(Long id);
    
}