package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.ship.dto.*;

import java.util.List;

public interface ShipService {
    ShipResponse deploy(ShipRequest request, Long cityId, Long userId);
    List<DeployedShip> getShipsInCity(Long cityId, Long userId);
    ExpeditionResponse sendShips(ExpeditionRequest request, Long cityId, Long userId);
    void unlockShips(ExpeditionEndEvent event);

    RepairResponse repairShip(RepairRequest request, Long shipId, Long userId);
}
