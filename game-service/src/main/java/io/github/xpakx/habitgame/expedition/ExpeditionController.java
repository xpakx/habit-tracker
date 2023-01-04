package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.ActionRequest;
import io.github.xpakx.habitgame.expedition.dto.ActionResponse;
import io.github.xpakx.habitgame.expedition.dto.ExpeditionResultResponse;
import io.github.xpakx.habitgame.expedition.dto.ExpeditionSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExpeditionController {
    private final ExpeditionService service;


    @GetMapping("/expedition/active")
    public ResponseEntity<List<ExpeditionSummary>> getActiveExpeditions(@RequestHeader String id) {
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

    @PostMapping("/expedition/{expeditionId}/complete")
    public ResponseEntity<ActionResponse> completeExpedition(@RequestBody ActionRequest request, @RequestHeader String id, @PathVariable Long expeditionId) {
        return new ResponseEntity<>(
                service.completeExpedition(request, expeditionId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @PostMapping("/expedition/{expeditionId}/return")
    public ResponseEntity<ActionResponse> returnToCity(@RequestBody ActionRequest request, @RequestHeader String id, @PathVariable Long expeditionId) {
        return new ResponseEntity<>(
                service.returnToCity(request, expeditionId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
