package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.ship.dto.ShipRequest;
import io.github.xpakx.habitcity.ship.dto.ShipResponse;

import java.util.List;

public interface ShipService {
    ShipResponse deploy(ShipRequest request, Long cityId, Long userId);
    List<Ship> getShipsInCity(Long cityId, Long userId);
}
