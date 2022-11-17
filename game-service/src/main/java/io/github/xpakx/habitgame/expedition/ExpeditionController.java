package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.ExpeditionResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExpeditionController {
    private final ExpeditionService service;


    @GetMapping("/expedition/active")
    public ResponseEntity<List<Expedition>> getActiveExpeditions(@RequestHeader String id) {
        return new ResponseEntity<>(
                service.getActiveExpeditions(Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/expedition/{expeditionId}/result")
    public ResponseEntity<ExpeditionResultResponse> getResult(@RequestHeader String id, @PathVariable Long expeditionId) {
        return new ResponseEntity<>(
                service.getResult(expeditionId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
