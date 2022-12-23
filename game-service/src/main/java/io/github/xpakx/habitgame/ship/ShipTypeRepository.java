package io.github.xpakx.habitgame.ship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipTypeRepository extends JpaRepository<ShipType, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM ship_type r WHERE r.rarity = :rarity ORDER BY RANDOM() LIMIT :amount")
    List<ShipType> findRandomTypes(int amount, int rarity);
}