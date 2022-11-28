package io.github.xpakx.habitgame.discovery;

import io.github.xpakx.habitgame.discovery.dto.DiscoveryResponse;
import io.github.xpakx.habitgame.discovery.dto.NamingIslandRequest;
import io.github.xpakx.habitgame.discovery.dto.NamingIslandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DiscoveryController {
    private final DiscoveryService service;

    @GetMapping("/expedition/{expeditionId}/island")
    public ResponseEntity<DiscoveryResponse> revealIsland(@RequestHeader String id, @PathVariable Long expeditionId) {
        return new ResponseEntity<>(
                service.revealIsland(expeditionId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @PostMapping("/expedition/{expeditionId}/island")
    public ResponseEntity<NamingIslandResponse> nameIsland(@RequestHeader String id, @PathVariable Long expeditionId, @RequestBody NamingIslandRequest request) {
        return new ResponseEntity<>(
                service.nameIsland(request, expeditionId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
