package io.github.xpakx.habitcity.ship;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerShipRepository extends JpaRepository<PlayerShip, Long> {
    List<PlayerShip> findByCityIdAndCityUserId(Long cityId, Long userId);
}