package io.github.xpakx.habitgame.discovery;

import io.github.xpakx.habitgame.discovery.dto.DiscoveryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DiscoveryController {
    private final DiscoveryService service;

    @GetMapping("/expedition/{expeditionId}/island")
    public ResponseEntity<DiscoveryResponse> getActiveExpeditions(@RequestHeader String id, @PathVariable Long expeditionId) {
        return new ResponseEntity<>(
                service.revealIsland(expeditionId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
