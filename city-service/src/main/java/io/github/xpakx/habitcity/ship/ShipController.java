package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.ship.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShipController {
    private final ShipService service;

    @PostMapping("/city/{cityId}/ship")
    public ResponseEntity<ShipResponse> deploy(@RequestBody ShipRequest request, @PathVariable Long cityId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.deploy(request, cityId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/city/{cityId}/ship/all")
    public ResponseEntity<List<DeployedShip>> getShips(@PathVariable Long cityId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.getShipsInCity(cityId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @PostMapping("/city/{cityId}/expedition")
    public ResponseEntity<ExpeditionResponse> sendShips(@RequestBody ExpeditionRequest request, @PathVariable Long cityId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.sendShips(request, cityId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @PostMapping("/city/ship/{shipId}/repair")
    public ResponseEntity<RepairResponse> repairShip(@RequestBody RepairRequest request, @PathVariable Long shipId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.repairShip(request, shipId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
