package io.github.xpakx.habitgame.island;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IslandRepository extends JpaRepository<Island, Long> {
    List<Island> findByUserId(Long userId);
}