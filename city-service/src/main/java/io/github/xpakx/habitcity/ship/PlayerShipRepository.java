package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.ship.dto.DeployedShip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlayerShipRepository extends JpaRepository<PlayerShip, Long> {
    List<DeployedShip> findByCityIdAndCityUserId(Long cityId, Long userId);
    List<PlayerShip> findByCityIdAndIdIn(Long cityId, Collection<Long> ids);

    Optional<PlayerShip> findByIdAndCityUserId(Long id, Long userId);
}