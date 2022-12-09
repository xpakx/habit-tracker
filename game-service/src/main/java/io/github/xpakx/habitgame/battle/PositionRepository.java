package io.github.xpakx.habitgame.battle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    boolean existsByXPosAndYPosAndBattleId(Integer x, Integer y, Long battleId);
    List<Position> findByBattleId(Long id);

}