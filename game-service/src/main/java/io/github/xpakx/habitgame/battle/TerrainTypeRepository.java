package io.github.xpakx.habitgame.battle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerrainTypeRepository extends JpaRepository<TerrainType, Long> {
    List<TerrainType> findBySeizableFalse();
}