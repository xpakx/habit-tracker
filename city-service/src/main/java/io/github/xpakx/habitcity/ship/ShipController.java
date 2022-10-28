package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.ship.dto.ShipRequest;
import io.github.xpakx.habitcity.ship.dto.ShipResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShipController {
    private final ShipService service;

    @PostMapping("/city/{cityId}/ship/build")
    public ResponseEntity<ShipResponse> build(@RequestBody ShipRequest request, @PathVariable Long cityId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.build(request, cityId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/city/{cityId}/ship/all")
    public ResponseEntity<List<Ship>> getShips(@PathVariable Long cityId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.getShipsInCity(cityId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
