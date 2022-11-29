package io.github.xpakx.habitgame.island;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IslandRepository extends JpaRepository<Island, Long> {
    List<Island> findByUserId(Long userId);

    Optional<Island> findByIdAndUserId(Long id, Long userId);
}